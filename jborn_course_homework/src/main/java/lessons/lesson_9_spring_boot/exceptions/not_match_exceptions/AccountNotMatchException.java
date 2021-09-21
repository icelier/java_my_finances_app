package lessons.lesson_9_spring_boot.exceptions.not_match_exceptions;

import lessons.lesson_9_spring_boot.exceptions.not_match_exceptions.NotMatchException;

public class AccountNotMatchException extends NotMatchException {
    public AccountNotMatchException() {
        super("Account state does not match");
    }

    public AccountNotMatchException(String msg) {
        super(msg);
    }
}
