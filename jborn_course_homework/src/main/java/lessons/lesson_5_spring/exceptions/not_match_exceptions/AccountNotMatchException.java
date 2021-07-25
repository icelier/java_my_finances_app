package lessons.lesson_5_spring.exceptions.not_match_exceptions;

public class AccountNotMatchException extends NotMatchException {
    public AccountNotMatchException() {
        super("Account state does not match");
    }

    public AccountNotMatchException(String msg) {
        super(msg);
    }
}
