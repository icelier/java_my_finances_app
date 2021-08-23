package lessons.lesson_9_spring_boot.exceptions.already_exists_exception;

import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.DataAlreadyExistsException;

public class UserAlreadyExistsException extends DataAlreadyExistsException {
    public UserAlreadyExistsException() {
        super("User already exists in the database");
    }

    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
