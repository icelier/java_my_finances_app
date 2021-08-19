package lessons.lesson_8_hibernate.exceptions.not_found_exception;

import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;

public class RoleNotFoundException extends DataNotFoundException {
    public RoleNotFoundException() {
        super("No such role in the database");
    }

    public RoleNotFoundException(String msg) {
        super(msg);
    }
}
