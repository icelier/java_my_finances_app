package lessons.lesson_6_servlets.exceptions.already_exists_exception;

public class AccountTypeAlreadyExistsException extends DataAlreadyExistsException {
    public AccountTypeAlreadyExistsException() {
        super("Account type already exists in the database");
    }

    public AccountTypeAlreadyExistsException(String msg) {
        super(msg);
    }
}
