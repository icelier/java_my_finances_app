package lessons.lesson_10_spring_security.exceptions.already_exists_exception;

public class DataAlreadyExistsException extends Exception {
    public DataAlreadyExistsException(String msg) {
        super(msg);
    }
}
