package lessons.lesson_4_add_layers_and_factories.exceptions.not_found_exception;

public class AccountNotFoundException extends DataNotFoundException {
    public AccountNotFoundException() {
        super("No such account in the database");
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
