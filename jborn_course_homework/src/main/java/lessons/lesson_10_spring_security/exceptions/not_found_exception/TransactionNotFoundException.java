package lessons.lesson_10_spring_security.exceptions.not_found_exception;

public class TransactionNotFoundException extends DataNotFoundException {
    public TransactionNotFoundException() {
        super("No such transaction in the database");
    }

    public TransactionNotFoundException(String msg) {
        super(msg);
    }
}
