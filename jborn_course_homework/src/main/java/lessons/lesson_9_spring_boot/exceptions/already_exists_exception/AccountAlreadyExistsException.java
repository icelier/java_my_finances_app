package lessons.lesson_9_spring_boot.exceptions.already_exists_exception;

import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.DataAlreadyExistsException;

public class AccountAlreadyExistsException extends DataAlreadyExistsException {
    public AccountAlreadyExistsException() {
        super("Account already exists in the database");
    }

    public AccountAlreadyExistsException(String msg) {
        super(msg);
    }
}
