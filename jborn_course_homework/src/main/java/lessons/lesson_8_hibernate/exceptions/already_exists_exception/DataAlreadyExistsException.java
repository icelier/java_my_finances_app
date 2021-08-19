package lessons.lesson_8_hibernate.exceptions.already_exists_exception;

public class DataAlreadyExistsException extends Exception {
    public DataAlreadyExistsException(String msg) {
        super(msg);
    }
}
