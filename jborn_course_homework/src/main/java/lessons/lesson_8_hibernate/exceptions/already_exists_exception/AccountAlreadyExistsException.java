package lessons.lesson_8_hibernate.exceptions.already_exists_exception;

import lessons.lesson_8_hibernate.exceptions.already_exists_exception.DataAlreadyExistsException;

public class AccountAlreadyExistsException extends DataAlreadyExistsException {
    public AccountAlreadyExistsException() {
        super("Account already exists in the database");
    }

    public AccountAlreadyExistsException(String msg) {
        super(msg);
    }
}
