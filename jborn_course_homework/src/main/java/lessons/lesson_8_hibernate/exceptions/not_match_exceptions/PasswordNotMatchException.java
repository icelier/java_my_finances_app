package lessons.lesson_8_hibernate.exceptions.not_match_exceptions;

import lessons.lesson_8_hibernate.exceptions.not_match_exceptions.NotMatchException;

public class PasswordNotMatchException extends NotMatchException {
    public PasswordNotMatchException() {
        super("Password does not match");
    }

    public PasswordNotMatchException(String msg) {
        super(msg);
    }
}
