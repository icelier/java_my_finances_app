package lessons.lesson_10_spring_security.controllers.users.authentication_utils.password_security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static lessons.lesson_10_spring_security.MyApplication.BASE_URL;

@Component("authenticationFailureHandler")
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException exception)
            throws IOException, ServletException {
        System.out.println("in CustomAuthenticationFailureHandler: onAuthenticationFailure");
        setDefaultFailureUrl(BASE_URL + "/users/login?error");

        super.onAuthenticationFailure(request, response, exception);

        String errorMessage = "Invalid username or password";

        if (exception.getMessage()
                .equalsIgnoreCase("User is disabled")) {
            errorMessage = "User is disabled";
        } else if (exception.getMessage()
                .equalsIgnoreCase("User account has expired")) {
            errorMessage = "User account has expired";
        } else if (exception.getMessage()
                .equalsIgnoreCase("blocked")) {
            errorMessage = "Blocked. Try in 24 h";
        } else if (exception.getMessage()
                .equalsIgnoreCase("unusual location")) {
            errorMessage = "unusual location";
        }

        request.getSession()
                .setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}
