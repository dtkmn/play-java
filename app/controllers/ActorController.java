package controllers;

import akka.actor.ActorRef;
import play.mvc.Controller;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

import static akka.pattern.Patterns.ask;

/**
 * Created by dtkmn on 17/02/2017.
 */

@Singleton
public class ActorController extends Controller {

//    final ActorRef helloActor;
//    @Inject
//    public ActorController(ActorSystem system) {
//        helloActor = system.actorOf(HelloActor.props);
//    }
//    public CompletionStage<Result> sayHello(String name) {
//        return FutureConverters.toJava(ask(helloActor, new HelloActorProtocol.SayHello(name), 1000))
//                .thenApply(response -> ok((String)response));
//    }

    private ActorRef configuredActor;
    private ActorRef helloActor;
    private ActorRef javaPongActor;

    @Inject
    public ActorController(@Named("configured-actor") ActorRef configuredActor,
                           @Named("hello-actor") ActorRef helloActor,
                           @Named("java-pong-actor") ActorRef javaPongActor) {
        this.configuredActor = configuredActor;
        this.helloActor = helloActor;
        this.javaPongActor = javaPongActor;
    }

//    public CompletionStage<Result> getConfig() {
    public Result getConfig() {
//        return FutureConverters.toJava(ask(helloActor, request(), 1000))
//                .thenApply(response -> ok((String)response));
        ask(javaPongActor, "dan2", 5000);
        return ok("Thanks!");
//                .thenApply(response -> {
//                    return ok((String)response);
//                });
    }



}
