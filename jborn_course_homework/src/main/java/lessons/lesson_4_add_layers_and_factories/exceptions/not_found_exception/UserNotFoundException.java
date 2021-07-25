package lessons.lesson_4_add_layers_and_factories.exceptions.not_found_exception;

public class UserNotFoundException extends DataNotFoundException {
    public UserNotFoundException() {
        super("No such user in the database");
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }
}
