package lessons.lesson_10_spring_security.exceptions.already_exists_exception;

public class AccountTypeAlreadyExistsException extends DataAlreadyExistsException {
    public AccountTypeAlreadyExistsException() {
        super("Account type already exists in the database");
    }

    public AccountTypeAlreadyExistsException(String msg) {
        super(msg);
    }
}
