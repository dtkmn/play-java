package akka.actors;

import actors.JavaPongActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.junit.Test;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import scala.concurrent.Future;


import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static scala.compat.java8.FutureConverters.*;
import static akka.pattern.Patterns.ask;

/**
 * Created by dtkmn on 1/04/2017.
 */
public class PongActorTest {

    WSClient ws = WS.newClient(8899);

    ActorSystem system = ActorSystem.create();
    ActorRef actorRef = system.actorOf(Props.create(JavaPongActor.class, ws));

    @Test
    public void shouldReplyToPingWithPong() throws Exception {
        Future sFuture = ask(actorRef, "Ping", 10000);
        final CompletionStage<String> cs = toJava(sFuture);
        final CompletableFuture<String> jFuture = (CompletableFuture<String>) cs;
        assert(jFuture.get(10000, TimeUnit.MILLISECONDS).equals("Pong"));
    }

    @Test
    public void shouldReplyToDanWithSth() throws Exception {
        Future sFuture = ask(actorRef, "dan2", 10000);
        final CompletionStage<Integer> cs = toJava(sFuture);
        final CompletableFuture<Integer> jFuture = (CompletableFuture<Integer>) cs;
        Thread.sleep(6000L);

        Future sFuture2 = ask(actorRef, "dan2", 10000);
        final CompletionStage<Integer> cs2 = toJava(sFuture2);
        final CompletableFuture<Integer> jFuture2 = (CompletableFuture<Integer>) cs2;
        Thread.sleep(6000L);

//        Future sFuture3 = ask(actorRef, "dan", 10000);
//        final CompletionStage<Integer> cs3 = toJava(sFuture3);
//        final CompletableFuture<Integer> jFuture3 = (CompletableFuture<Integer>) cs3;
        Thread.sleep(6000L);
    }

    @Test(expected = ExecutionException.class)
    public void shouldReplyToUnknownMessageWithFailure() throws Exception {
        Future sFuture = ask(actorRef, "unknown", 10000);
        final CompletionStage<String> cs = toJava(sFuture);
        final CompletableFuture<String> jFuture = (CompletableFuture<String>) cs;
        jFuture.get(10000, TimeUnit.MILLISECONDS);
    }



    public CompletionStage<String> askPong(String message) {
        Future sFuture = ask(actorRef, "Ping", 1000);
        CompletionStage<String> cs = toJava(sFuture);
        return cs;
    }

    @Test
    public void printToConsole() throws Exception {
        askPong("Ping")
            .thenApply(x -> x.charAt(0))
            .thenCompose(x -> askPong("Ping"))
            .thenCombine(askPong("Ping"), (a, b) -> a+b)
            .thenAccept(x -> System.out.println("replied with: " + x));
//            .exceptionally(t -> System.out.println("error"))
        Thread.sleep(100);
    }

}
