package lessons.lesson_7_controllers.exceptions.operation_failed;

public class OperationFailedException extends Exception {
    public OperationFailedException(String msg) {
        super(msg);
    }
}
