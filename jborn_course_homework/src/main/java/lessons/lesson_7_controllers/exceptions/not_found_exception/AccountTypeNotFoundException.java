package lessons.lesson_7_controllers.exceptions.not_found_exception;

public class AccountTypeNotFoundException extends DataNotFoundException {
    public AccountTypeNotFoundException() {
        super("No such account type in the database");
    }

    public AccountTypeNotFoundException(String msg) {
        super(msg);
    }
}
