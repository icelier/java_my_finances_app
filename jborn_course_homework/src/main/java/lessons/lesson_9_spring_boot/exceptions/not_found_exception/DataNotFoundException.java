package lessons.lesson_9_spring_boot.exceptions.not_found_exception;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
