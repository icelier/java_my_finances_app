package lessons.lesson_4_add_layers_and_factories.exceptions.already_exists_exception;

public class UserAlreadyExistsException extends DataAlreadyExistsException {
    public UserAlreadyExistsException() {
        super("User already exists in the database");
    }

    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
