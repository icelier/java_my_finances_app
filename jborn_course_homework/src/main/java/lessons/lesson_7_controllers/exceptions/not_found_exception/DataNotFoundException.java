package lessons.lesson_7_controllers.exceptions.not_found_exception;

public class DataNotFoundException extends Exception {
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
