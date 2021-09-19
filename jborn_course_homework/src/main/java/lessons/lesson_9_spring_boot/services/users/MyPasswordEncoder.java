package lessons.lesson_9_spring_boot.services.users;

import lessons.lesson_9_spring_boot.exceptions.not_match_exceptions.PasswordNotMatchException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

@Resource
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
public class MyPasswordEncoder {
    private static final int MAX_TRY = 3;

    private int tryCount;

    @Autowired private BCryptPasswordEncoder encoder;

    public boolean matches(CharSequence passwordTry, String passwordFromDb) throws PasswordNotMatchException {
        ++tryCount;
        boolean matches = encoder.matches(passwordTry, passwordFromDb);

        if (tryCount >= MAX_TRY && !matches) {
            throw new PasswordNotMatchException("Attempts exhausted");
        }

        return matches;
    }

    public String encode(CharSequence rawPassword) {
        return encoder.encode(rawPassword);
    }
}
