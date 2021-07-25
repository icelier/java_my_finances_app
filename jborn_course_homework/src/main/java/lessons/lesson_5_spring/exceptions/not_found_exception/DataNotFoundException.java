package lessons.lesson_5_spring.exceptions.not_found_exception;

public class DataNotFoundException extends Exception {
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
