package lessons.lesson_9_spring_boot.exceptions.not_found_exception;

public class DataNotFoundException extends Exception {
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
