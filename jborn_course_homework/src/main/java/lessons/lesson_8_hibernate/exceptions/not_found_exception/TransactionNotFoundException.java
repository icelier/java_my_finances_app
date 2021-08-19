package lessons.lesson_8_hibernate.exceptions.not_found_exception;

import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;

public class TransactionNotFoundException extends DataNotFoundException {
    public TransactionNotFoundException() {
        super("No such transaction in the database");
    }

    public TransactionNotFoundException(String msg) {
        super(msg);
    }
}
