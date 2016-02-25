package rationale.emotions.servlets.node;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rationale.emotions.threads.HookExplorer;
import rationale.emotions.threads.ThreadInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class ListShutdownHooks extends HttpServlet {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ThreadInfo> hooks = HookExplorer.getShutdownHooksInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(200);
        resp.getWriter().write(gson.toJson(hooks));
    }
}
