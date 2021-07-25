package lessons.lesson_4_add_layers_and_factories.exceptions.already_exists_exception;

public class TransactionAlreadyExistsException extends DataAlreadyExistsException {
    public TransactionAlreadyExistsException() {
        super("Transaction already exists in the database");
    }

    public TransactionAlreadyExistsException(String msg) {
        super(msg);
    }
}
