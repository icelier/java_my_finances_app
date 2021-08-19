package lessons.lesson_8_hibernate.exceptions.not_found_exception;

public class DataNotFoundException extends Exception {
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
