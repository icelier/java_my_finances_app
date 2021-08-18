package lessons.lesson_6_servlets.exceptions.already_exists_exception;

public class DataAlreadyExistsException extends Exception {
    public DataAlreadyExistsException(String msg) {
        super(msg);
    }
}
