package lessons.lesson_8_hibernate.exceptions.already_exists_exception;

import lessons.lesson_8_hibernate.exceptions.already_exists_exception.DataAlreadyExistsException;

public class RoleAlreadyExistsException extends DataAlreadyExistsException {
    public RoleAlreadyExistsException() {
        super("Role already exists in the database");
    }

    public RoleAlreadyExistsException(String msg) {
        super(msg);
    }
}
