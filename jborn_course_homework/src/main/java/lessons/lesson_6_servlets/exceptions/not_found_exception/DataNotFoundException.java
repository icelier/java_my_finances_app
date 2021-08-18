package lessons.lesson_6_servlets.exceptions.not_found_exception;

public class DataNotFoundException extends Exception {
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
