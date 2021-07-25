package lessons.lesson_5_spring.exceptions.already_exists_exception;

public class DataAlreadyExistsException extends Exception {
    public DataAlreadyExistsException(String msg) {
        super(msg);
    }
}
