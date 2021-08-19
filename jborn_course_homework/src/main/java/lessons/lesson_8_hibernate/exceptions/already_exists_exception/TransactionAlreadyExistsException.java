package lessons.lesson_8_hibernate.exceptions.already_exists_exception;

import lessons.lesson_8_hibernate.exceptions.already_exists_exception.DataAlreadyExistsException;

public class TransactionAlreadyExistsException extends DataAlreadyExistsException {
    public TransactionAlreadyExistsException() {
        super("Transaction already exists in the database");
    }

    public TransactionAlreadyExistsException(String msg) {
        super(msg);
    }
}
