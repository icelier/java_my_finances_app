package lessons.lesson_10_spring_security.exceptions.not_found_exception;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
