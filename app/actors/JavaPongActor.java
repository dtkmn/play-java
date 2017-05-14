package actors;

import akka.actor.AbstractActor;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.CircuitBreaker;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import scala.PartialFunction;
import scala.concurrent.duration.FiniteDuration;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by dtkmn on 1/04/2017.
 */
public class JavaPongActor extends AbstractActor {

    private WSClient ws;
    private Set<String> downstreams = new HashSet<>();
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Inject
    public JavaPongActor(WSClient ws) {
        this.ws = ws;
    }

    public void notifyMeOnOpen(String downstream) {
        log.info("CircuitBreaker for downstream {} is now open.", downstream);
    }
//    public void notifyMeOnHalfOpen() {
//        log.info("My CircuitBreaker is now half open");
//        breaker.callWithCircuitBreakerCS(this::wsLoad);
//    }
    public void notifyMeOnClose(String downstream) {
        log.info("CircuitBreaker for downstream {} is now close.", downstream);
    }

    @Override
    public PartialFunction receive() {
        return ReceiveBuilder
//            .matchEquals("Ping", s -> {
//                sender().tell("Pong", ActorRef.noSender());
//                CompletableFuture<Integer> completableFuture = wsLoad();
//                completableFuture.thenAccept(body -> {
//                    System.out.println(body);
//                });
//            })
//            .match(String.class, "dan"::equals, m -> pipe(
//                breaker.callWithCircuitBreakerCS(this::wsLoad),
//                    getContext().dispatcher()
//            ).to(sender()))
//            .match(String.class, "dan2"::equals, m -> pipe(
//                breaker.callWithCircuitBreakerCS(this::wsLoad2),
//                    getContext().dispatcher()
//            ).to(sender()))
//            .match(SuspendRouteRequest.class, request -> {
//                String sourceUrl = request.getSourceUrl();
//                breaker.callWithCircuitBreakerCS(() -> wsLoad(sourceUrl));
//            })
            .match(JsonNode.class, json -> !Strings.isNullOrEmpty(json.get("downstream").textValue()), json -> {
                String downstream = json.get("downstream").textValue();
                if(!downstreams.contains(downstream)) {
                    createCircuitBreaker(downstream);
                    sender().tell(new Status.Success("Circuit Breaker created for " + downstream), self());
                } else {
                    sender().tell(new Status.Success("No action invoked. Circuit Breaker already created for " + downstream), self());
                }
            })
            .matchAny(x ->
                sender().tell( new Status.Success("Bad request"), self() )
            )
            .build();
    }

    private CircuitBreaker createCircuitBreaker(String downstream) {
        downstreams.add(downstream);
        CircuitBreaker circuitBreaker = new CircuitBreaker(
            getContext().dispatcher(), getContext().system().scheduler(), 1,
            FiniteDuration.create(2, TimeUnit.SECONDS), FiniteDuration.create(20, TimeUnit.SECONDS));
        circuitBreaker
            .onOpen(() -> log.debug("CircuitBreaker for downstream '{}' is now open.", downstream))
            .onHalfOpen(() -> {
                log.debug("CircuitBreaker for downstream '{}' is now half open.", downstream);
                circuitBreaker.succeed();
                downstreams.remove(downstream);
            })
            .onClose(() -> log.debug("CircuitBreaker for downstream '{}' is now close.", downstream));
        circuitBreaker.fail();
        return circuitBreaker;
    }

    private CompletableFuture<Integer> wsLoad(String sourceUrl) {
        return ws.url(sourceUrl)
            .get()
            .thenApply(WSResponse::getStatus)
            .toCompletableFuture();
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
