import org.junit.Test;
import play.twirl.api.Content;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 *
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 *
 */
public class ApplicationTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertEquals(2, a);
    }

    @Test
    public void renderTemplate() {
        Content html = views.html.index.render("Your new application is ready.");
        assertEquals("text/html", html.contentType());
        assertTrue(html.body().contains("Your new application is ready."));
    }

    @Test
    public void testCompleteableFuture() {
        func1("ewer")
            .thenApply(this::validateInput)
            .thenCompose(this::func2)
            .thenApply(Object::toString)
                .thenApply(s -> {
                    System.out.println(s);
                    return Integer.parseInt(s);
                }).thenApply(i -> i+1);
    }

    private CompletableFuture<String> func1(String input) {
        return CompletableFuture.supplyAsync(() -> input);
    }

    private int validateInput(String test) {
        try {
            return Integer.parseInt(test);
        } catch(Exception e) {
            return 0;
        }
    }

    private CompletableFuture<Integer> func2(int s) {
        return CompletableFuture.supplyAsync(() -> s);
    }

}
