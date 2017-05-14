package controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Controller;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;

/**
 * Created by dtkmn on 17/02/2017.
 */

@Singleton
public class ActorController extends Controller {

    private ActorRef javaPongActor;

    @Inject
    public ActorController(@Named("java-pong-actor") ActorRef javaPongActor) {
        this.javaPongActor = javaPongActor;
    }

    public CompletionStage<Result> getConfig() {

//        final ActorRef extraActorRef = actorSystem.actorOf(Props.create(ExtraActor.class));

        final JsonNode suspendRouteRequest = request().body().asJson();
        return FutureConverters.toJava(ask(javaPongActor, suspendRouteRequest, 5000))
            .thenApply(response -> {
                return ok(response.toString());
//                    System.out.println(" [x] response received and show on controller - " + response);
            });

//        for(int i=0; i<100000; i++) {
//            JsonNode node = Json.parse("{\"downstream\": \"i" + i + "\"}");
//            FutureConverters.toJava(ask(javaPongActor, node, 5000))
//                    .thenAccept(System.out::println);
//        }

//        ActorRef myActor = actorSystem.actorOf(new Props(new UntypedActorFactory() {
//            public UntypedActor create() {
////                return new MyActor("...");
//            }
//        }), "myactor");

//        javaPongActor.tell(suspendRouteRequest, extraActorRef);

//        return CompletableFuture.supplyAsync(() -> ok());
//        javaPongActor.tell(request().body().asText(), ActorRef.noSender());

    }

//    CompletableFuture<String> future = new CompletableFuture<>();
//    class ExtraActor extends AbstractActor {
//
//        @Override
//        public PartialFunction receive() {
//            return ReceiveBuilder
//                .matchAny(x -> {
//                    sender().tell("DONE!", self());
//                })
//                .build();
//        }
//    }



}
