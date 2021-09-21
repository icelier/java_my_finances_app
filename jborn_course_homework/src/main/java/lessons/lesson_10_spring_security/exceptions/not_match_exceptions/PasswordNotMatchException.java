package lessons.lesson_10_spring_security.exceptions.not_match_exceptions;

public class PasswordNotMatchException extends NotMatchException {
    public PasswordNotMatchException() {
        super("Password does not match");
    }

    public PasswordNotMatchException(String msg) {
        super(msg);
    }
}
