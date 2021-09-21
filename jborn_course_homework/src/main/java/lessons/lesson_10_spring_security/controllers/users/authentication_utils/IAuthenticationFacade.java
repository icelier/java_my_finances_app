package lessons.lesson_10_spring_security.controllers.users.authentication_utils;

import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
}
