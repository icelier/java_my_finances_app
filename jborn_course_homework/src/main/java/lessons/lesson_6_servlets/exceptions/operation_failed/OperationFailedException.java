package lessons.lesson_6_servlets.exceptions.operation_failed;

public class OperationFailedException extends Exception {
    public OperationFailedException(String msg) {
        super(msg);
    }
}
