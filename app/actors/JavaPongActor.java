package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Status;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.CircuitBreaker;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import scala.PartialFunction;
import scala.concurrent.duration.FiniteDuration;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.pipe;

/**
 * Created by dtkmn on 1/04/2017.
 */
public class JavaPongActor extends AbstractActor {

    private WSClient ws;
    private CircuitBreaker breaker;

    @Inject
    public JavaPongActor(WSClient ws) {
        this.ws = ws;

        this.breaker = new CircuitBreaker(
                getContext().dispatcher(), getContext().system().scheduler(), 1,
                FiniteDuration.create(2, TimeUnit.SECONDS), FiniteDuration.create(10, TimeUnit.SECONDS));

        this.breaker.onOpen(this::notifyMeOnOpen);
        this.breaker.onHalfOpen(this::notifyMeOnHalfOpen);
        this.breaker.onClose(this::notifyMeOnClose);

    }

    public void notifyMeOnOpen() {
        System.out.println("My CircuitBreaker is now open");
    }
    public void notifyMeOnHalfOpen() {
        System.out.println("My CircuitBreaker is now half open");
        breaker.callWithCircuitBreakerCS(this::wsLoad);
    }
    public void notifyMeOnClose() {
        System.out.println("My CircuitBreaker is now close");
    }

    public PartialFunction receive() {
        return ReceiveBuilder
            .matchEquals("Ping", s -> {
                sender().tell("Pong", ActorRef.noSender());
                CompletableFuture<Integer> completableFuture = wsLoad();
                completableFuture.thenAccept(body -> {
                    System.out.println(body);
                });
            })
            .match(String.class, "dan"::equals, m -> pipe(
                breaker.callWithCircuitBreakerCS(this::wsLoad),
                    getContext().dispatcher()
            ).to(sender()))
            .match(String.class, "dan2"::equals, m -> pipe(
                breaker.callWithCircuitBreakerCS(this::wsLoad2),
                    getContext().dispatcher()
            ).to(sender()))
            .match(String.class, "block"::equals, m -> {
                breaker.fail();
            })
            .match(String.class, "succeed"::equals, m -> {
                breaker.succeed();
            })
            .matchAny(x -> sender().tell( new Status.Failure(new Exception("unknown message")), self() ))
            .build();
    }

    // should be fine
    private CompletableFuture<Integer> wsLoad() {
        return ws.url("http://google.com")
                .get()
                .thenApply(WSResponse::getStatus)
                .toCompletableFuture();
    }

    // should throw exception
    private CompletableFuture<Integer> wsLoad2() {
        return ws.url("http://google.com5")
                .get()
                .thenApply(WSResponse::getStatus)
                .toCompletableFuture();
    }

}
