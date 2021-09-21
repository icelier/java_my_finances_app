package lessons.lesson_10_spring_security.exceptions.already_exists_exception;

public class UserAlreadyExistsException extends DataAlreadyExistsException {
    public UserAlreadyExistsException() {
        super("User already exists in the database");
    }

    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
