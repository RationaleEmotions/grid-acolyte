package rationale.emotions.servlets.node;

import rationale.emotions.threads.HookExplorer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This servlet leverages the concept that has been explained in <a href="http://java.jiderhamn
 * .se/2012/01/01/classloader-leaks-ii-find-and-work-around-unwanted-references/">this</a> blog.
 * The idea behind creating this servlet arose out of
 * <a href="https://github.com/SeleniumHQ/selenium/issues/1679">this</a> GITHUB issue.
 */
public class OnDemandShutdownhookCleaner extends HttpServlet {
    static final String SELENIUM_SHUTDOWN_HOOK = "org.openqa.selenium.io.TemporaryFilesystem";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(resp);
    }

    private void process(HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(200);
        resp.getWriter().write("Number of hooks : " + HookExplorer.getShutdownHooks().size() + "\n");
        for (Thread thread : HookExplorer.getShutdownHooks()) {
            resp.getWriter().write("Working with the thread " + thread.toString() +
                " with its class as " + thread.getClass() + "\n");
            if (thread.getClass().getName().contains(SELENIUM_SHUTDOWN_HOOK)) {
                resp.getWriter().write("***Found the thread to clean***\n");
                thread.start();
                try {
                    thread.join(30 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Runtime.getRuntime().removeShutdownHook(thread);
                resp.getWriter().write("***Cleaned the thread***");
            }
        }
    }

}

