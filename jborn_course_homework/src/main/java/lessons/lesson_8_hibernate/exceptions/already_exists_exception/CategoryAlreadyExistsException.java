package lessons.lesson_8_hibernate.exceptions.already_exists_exception;

import lessons.lesson_8_hibernate.exceptions.already_exists_exception.DataAlreadyExistsException;

public class CategoryAlreadyExistsException extends DataAlreadyExistsException {
    public CategoryAlreadyExistsException() {
        super("Trasaction category already exists in the database");
    }

    public CategoryAlreadyExistsException(String msg) {
        super(msg);
    }
}
