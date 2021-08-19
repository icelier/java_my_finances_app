package lessons.lesson_8_hibernate.exceptions.not_match_exceptions;

import lessons.lesson_8_hibernate.exceptions.not_match_exceptions.NotMatchException;

public class AccountNotMatchException extends NotMatchException {
    public AccountNotMatchException() {
        super("Account state does not match");
    }

    public AccountNotMatchException(String msg) {
        super(msg);
    }
}
