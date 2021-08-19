package lessons.lesson_8_hibernate.exceptions.not_found_exception;

import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;

public class AccountNotFoundException extends DataNotFoundException {
    public AccountNotFoundException() {
        super("No such account in the database");
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
