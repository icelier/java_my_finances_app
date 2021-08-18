package lessons.lesson_6_servlets.exceptions.not_match_exceptions;

public class PasswordNotMatchException extends NotMatchException {
    public PasswordNotMatchException() {
        super("Password does not match");
    }

    public PasswordNotMatchException(String msg) {
        super(msg);
    }
}
