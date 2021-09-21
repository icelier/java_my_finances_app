package lessons.lesson_10_spring_security.exceptions.already_exists_exception;

public class AccountAlreadyExistsException extends DataAlreadyExistsException {
    public AccountAlreadyExistsException() {
        super("Account already exists in the database");
    }

    public AccountAlreadyExistsException(String msg) {
        super(msg);
    }
}
