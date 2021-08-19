package lessons.lesson_8_hibernate.servlets;

import lessons.lesson_8_hibernate.entities.users.UserEntity;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_8_hibernate.services.users.UserService;
import lessons.lesson_8_hibernate.servlets.TransactionServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {
    public static final String PATH = "/my-finances/login";
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

        if (userName == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Long userId = null;
        try {
            UserEntity user = userService.findByUserName(userName);
             userId = null;
            if (user == null) {
                UserEntity newUser = new UserEntity(userName, password, email);
                userId = userService.insert(newUser).getId();
            } else {
                userId = user.getId();
            }
        } catch (SQLException | OperationFailedException | UserAlreadyExistsException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            //            req.setAttribute("userId", userId);
//            RequestDispatcher dispatcher = req.getRequestDispatcher(TransactionServlet.PATH);
//            dispatcher.forward(req, resp);
            req.getSession().setAttribute("userId", userId);
            resp.sendRedirect(TransactionServlet.PATH);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        if (userName == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Long userId = null;
        try {
            UserEntity user = userService.findByUserName(userName);
            userId = null;
            if (user == null) {
                UserEntity newUser = new UserEntity(userName, password, email);
                userId = userService.insert(newUser).getId();
            } else {
                userId = user.getId();
            }
        } catch (SQLException | OperationFailedException | UserAlreadyExistsException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            req.getSession().setAttribute("userId", userId);
            resp.sendRedirect(TransactionServlet.PATH);
        }
    }
}
