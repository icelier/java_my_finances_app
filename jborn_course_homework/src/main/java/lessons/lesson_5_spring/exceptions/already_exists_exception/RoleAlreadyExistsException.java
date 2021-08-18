package lessons.lesson_5_spring.exceptions.already_exists_exception;

public class RoleAlreadyExistsException extends DataAlreadyExistsException {
    public RoleAlreadyExistsException() {
        super("Role already exists in the database");
    }

    public RoleAlreadyExistsException(String msg) {
        super(msg);
    }
}
