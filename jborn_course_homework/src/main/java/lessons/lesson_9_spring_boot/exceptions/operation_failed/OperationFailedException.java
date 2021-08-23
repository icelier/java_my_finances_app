package lessons.lesson_9_spring_boot.exceptions.operation_failed;

public class OperationFailedException extends Exception {
    public OperationFailedException(String msg) {
        super(msg);
    }
}
