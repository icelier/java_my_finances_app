package lessons.lesson_7_controllers.exceptions.already_exists_exception;

public class CategoryAlreadyExistsException extends DataAlreadyExistsException {
    public CategoryAlreadyExistsException() {
        super("Trasaction category already exists in the database");
    }

    public CategoryAlreadyExistsException(String msg) {
        super(msg);
    }
}
