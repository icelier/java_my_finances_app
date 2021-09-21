package lessons.lesson_10_spring_security.controllers.users.authentication_utils.password_security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationFailureListener
        implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @EventListener
    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        System.out.println("in AuthenticationFailureListener: onApplicationEvent");
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            System.out.println("onApplicationEvent: xfHeader = null");
            loginAttemptService.loginFailed(request.getRemoteAddr());
        } else {
            System.out.println("onApplicationEvent: xfHeader != null");
            loginAttemptService.loginFailed(xfHeader.split(",")[0]);
        }
    }
}
