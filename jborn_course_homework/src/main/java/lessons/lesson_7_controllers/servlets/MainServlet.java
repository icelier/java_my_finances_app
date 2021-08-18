package lessons.lesson_7_controllers.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import lessons.lesson_7_controllers.controllers.Controller;
import lessons.lesson_7_controllers.controllers.ControllerConfiguration;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@Import(ControllerConfiguration.class)
@Configuration
public class MainServlet extends HttpServlet {
    private static final String BASE_URL = "/my-finances/";
    private WebApplicationContext context;
    private final ObjectMapper objectMapper;

    public MainServlet() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void init() throws ServletException {
        super.init();

//        to link servlet container with spring context and use autowired
        context = WebApplicationContextUtils
                .getWebApplicationContext(getServletContext());
        AutowireCapableBeanFactory ctx = context.getAutowireCapableBeanFactory();
        ctx.autowireBean(this);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String path = uri.replace(BASE_URL, "");

        Controller controller = (Controller) context.getBean(path);
        if (controller == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            Object request = objectMapper.readValue(req.getInputStream(), controller.getRequestClass());
            Object response = controller.execute(request);
            objectMapper.writeValue(resp.getWriter(), response);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getLocalizedMessage());
        }
    }
}
