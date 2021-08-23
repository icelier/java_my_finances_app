package lessons.lesson_9_spring_boot.exceptions.not_found_exception;

import lessons.lesson_9_spring_boot.exceptions.not_found_exception.DataNotFoundException;

public class CategoryNotFoundException extends DataNotFoundException {
    public CategoryNotFoundException() {
        super("No such transaction category in the database");
    }

    public CategoryNotFoundException(String msg) {
        super(msg);
    }
}
