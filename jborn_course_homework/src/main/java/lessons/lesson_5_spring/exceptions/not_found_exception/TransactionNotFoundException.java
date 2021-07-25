package lessons.lesson_5_spring.exceptions.not_found_exception;

public class TransactionNotFoundException extends DataNotFoundException {
    public TransactionNotFoundException() {
        super("No such transaction in the database");
    }

    public TransactionNotFoundException(String msg) {
        super(msg);
    }
}
