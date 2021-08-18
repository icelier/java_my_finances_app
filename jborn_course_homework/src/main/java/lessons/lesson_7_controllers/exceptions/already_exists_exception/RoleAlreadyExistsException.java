package lessons.lesson_7_controllers.exceptions.already_exists_exception;

public class RoleAlreadyExistsException extends DataAlreadyExistsException {
    public RoleAlreadyExistsException() {
        super("Role already exists in the database");
    }

    public RoleAlreadyExistsException(String msg) {
        super(msg);
    }
}
