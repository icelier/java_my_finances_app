package lessons.lesson_5_spring.exceptions.operation_failed;

public class OperationFailedException extends Exception {
    public OperationFailedException(String msg) {
        super(msg);
    }
}
