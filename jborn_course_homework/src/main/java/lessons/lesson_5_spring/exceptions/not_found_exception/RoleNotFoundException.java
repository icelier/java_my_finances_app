package lessons.lesson_5_spring.exceptions.not_found_exception;

public class RoleNotFoundException extends DataNotFoundException {
    public RoleNotFoundException() {
        super("No such role in the database");
    }

    public RoleNotFoundException(String msg) {
        super(msg);
    }
}
