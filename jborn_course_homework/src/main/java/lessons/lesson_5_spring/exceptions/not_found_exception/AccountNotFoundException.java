package lessons.lesson_5_spring.exceptions.not_found_exception;

public class AccountNotFoundException extends DataNotFoundException {
    public AccountNotFoundException() {
        super("No such account in the database");
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
