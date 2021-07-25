package lessons.lesson_4_add_layers_and_factories.exceptions.not_found_exception;

public class TransactionNotFoundException extends DataNotFoundException {
    public TransactionNotFoundException() {
        super("No such transaction in the database");
    }

    public TransactionNotFoundException(String msg) {
        super(msg);
    }
}
