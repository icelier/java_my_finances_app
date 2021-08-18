package lessons.lesson_7_controllers.exceptions.already_exists_exception;

public class DataAlreadyExistsException extends Exception {
    public DataAlreadyExistsException(String msg) {
        super(msg);
    }
}
