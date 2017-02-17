package controllers;

import org.apache.commons.io.FileUtils;
import play.api.Play;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
//        Play.resourceAsStream("", Play.current());
        File file = Play.getFile("public/images/taskmate_logo.png", Play.current());
        Play.current().getFile("public/images/taskmate_logo.png");
        Play.resourceAsStream("taskmate_logo.png", Play.current());
        return ok(file).as("image/png");
//        return ok(index.render("Your new application is ready."));
    }

    public void intensiveComputation() {
        System.out.println("testing");
    }

    public CompletionStage<Result> getImageInFuture() {
        return CompletableFuture.supplyAsync(() -> {
            File file = Play.getFile("public/images/taskmate_logo.png", Play.current());
            if(file.exists()) {
                try {
                    byte[] bytes = FileUtils.readFileToByteArray(file);
                    Play.application(Play.current()).getFile("public/images/taskmate_logo.png");
                    return ok(bytes).as("image/png");
                } catch (IOException e) {
                    e.printStackTrace();
                    return badRequest(e.getMessage());
                }
            }
            return ok();
        });
    }

    public Result index2() {

        Integer test1;
        try {
            test1 = 2;
        } catch(Exception e) {
            test1 = 0;
        }
        System.out.println(test1);

        return ok(index.render("Your new application is ready."));
    }




}
