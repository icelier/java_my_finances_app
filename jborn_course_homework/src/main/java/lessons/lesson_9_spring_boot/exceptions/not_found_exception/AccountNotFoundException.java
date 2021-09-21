package lessons.lesson_9_spring_boot.exceptions.not_found_exception;

import lessons.lesson_9_spring_boot.exceptions.not_found_exception.DataNotFoundException;

public class AccountNotFoundException extends DataNotFoundException {
    public AccountNotFoundException() {
        super("No such account in the database");
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
