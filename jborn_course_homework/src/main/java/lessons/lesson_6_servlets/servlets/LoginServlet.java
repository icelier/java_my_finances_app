package lessons.lesson_6_servlets.servlets;

import lessons.lesson_6_servlets.entities.users.UserEntity;
import lessons.lesson_6_servlets.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_6_servlets.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_6_servlets.services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@Controller
public class LoginServlet extends HttpServlet {
    private AutowireCapableBeanFactory ctx;

    @Autowired
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();

//        to link servlet container with spring context and use autowired
        WebApplicationContext context = WebApplicationContextUtils
                .getWebApplicationContext(getServletContext());
        ctx = context.getAutowireCapableBeanFactory();
        ctx.autowireBean(this);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        try {
            UserEntity userFromDb = userService.findByUserName(userName);
            if (userFromDb == null) {
                UserEntity newUser = new UserEntity(userName, password, email);

                Long userId = userService.insert(newUser).getId();
                HttpSession session = req.getSession();
                session.setAttribute("userId", userId);
            }
        } catch (UserAlreadyExistsException e) {
            PrintWriter writer = resp.getWriter();
            writer.println("You are already registered!");
                } catch (SQLException throwables) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } catch (OperationFailedException e) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        Long userId = null;
        try {
            UserEntity userFromDb = userService.findByUserName(userName);
            if (userFromDb == null) {
                UserEntity newUser = new UserEntity(userName, password, email);
                userId = userService.insert(newUser).getId();
                HttpSession session = req.getSession();
                session.setAttribute("userId", userId);
            } else {
                // TODO -?
            }
        } catch (UserAlreadyExistsException e) {
                e.printStackTrace();
                // TODO - ?
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }
}
