package lessons.lesson_6_servlets.exceptions.already_exists_exception;

public class TransactionAlreadyExistsException extends DataAlreadyExistsException {
    public TransactionAlreadyExistsException() {
        super("Transaction already exists in the database");
    }

    public TransactionAlreadyExistsException(String msg) {
        super(msg);
    }
}
