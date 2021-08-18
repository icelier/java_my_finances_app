package lessons.lesson_7_controllers.servlets;

import lessons.lesson_7_controllers.entities.finances.Transaction;
import lessons.lesson_7_controllers.services.finances.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class TransactionServlet extends HttpServlet {
    public static final String PATH = "/my-finances/transactions";
    private AutowireCapableBeanFactory ctx;

    @Autowired
    private TransactionService transactionService;

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

            Long userId = (Long) req.getSession().getAttribute("userId");
            try {
                List<Transaction> userTransactions = transactionService.findAllByUserId(userId);
                PrintWriter writer = resp.getWriter();
                for (Transaction transaction: userTransactions) {
                    writer.println(transaction);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}
