package lessons.lesson_8_hibernate.exceptions.not_found_exception;

import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;

public class UserNotFoundException extends DataNotFoundException {
    public UserNotFoundException() {
        super("No such user in the database");
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }
}
