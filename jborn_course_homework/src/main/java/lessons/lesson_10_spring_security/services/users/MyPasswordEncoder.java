package lessons.lesson_10_spring_security.services.users;

import lessons.lesson_10_spring_security.exceptions.not_match_exceptions.PasswordNotMatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//@Resource
//@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
//@Component
public class MyPasswordEncoder {
    private static final int MAX_TRY = 3;

    private int tryCount;

//    @Autowired
    private BCryptPasswordEncoder encoder;

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
