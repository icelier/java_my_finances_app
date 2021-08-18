package lessons.lesson_5_spring.exceptions.not_found_exception;

public class UserNotFoundException extends DataNotFoundException {
    public UserNotFoundException() {
        super("No such user in the database");
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }
}
