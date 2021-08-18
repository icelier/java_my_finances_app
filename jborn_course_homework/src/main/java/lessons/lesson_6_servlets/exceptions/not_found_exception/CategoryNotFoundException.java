package lessons.lesson_6_servlets.exceptions.not_found_exception;

public class CategoryNotFoundException extends DataNotFoundException {
    public CategoryNotFoundException() {
        super("No such transaction category in the database");
    }

    public CategoryNotFoundException(String msg) {
        super(msg);
    }
}
