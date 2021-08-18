package lessons.lesson_6_servlets.servlets;

import org.springframework.stereotype.Controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();

        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect("/my-finances/login");
//            RequestDispatcher view = req.getRequestDispatcher("webapp/login.html");
//            view.forward(req, resp);
        } else {
            writer.println("Hello, " + req.getParameter("username") + "!");
        }
    }

}
