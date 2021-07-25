package lessons.lesson_4_add_layers_and_factories.exceptions.not_match_exceptions;

public class AccountNotMatchException extends NotMatchException {
    public AccountNotMatchException() {
        super("Account state does not match");
    }

    public AccountNotMatchException(String msg) {
        super(msg);
    }
}
