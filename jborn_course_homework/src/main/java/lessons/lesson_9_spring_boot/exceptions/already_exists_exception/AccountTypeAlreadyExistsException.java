package lessons.lesson_9_spring_boot.exceptions.already_exists_exception;

import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.DataAlreadyExistsException;

public class AccountTypeAlreadyExistsException extends DataAlreadyExistsException {
    public AccountTypeAlreadyExistsException() {
        super("Account type already exists in the database");
    }

    public AccountTypeAlreadyExistsException(String msg) {
        super(msg);
    }
}
