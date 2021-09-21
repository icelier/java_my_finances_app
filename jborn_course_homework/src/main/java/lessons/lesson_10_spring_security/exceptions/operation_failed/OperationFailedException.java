package lessons.lesson_10_spring_security.exceptions.operation_failed;

public class OperationFailedException extends RuntimeException {
    public OperationFailedException(String msg) {
        super(msg);
    }
}
