package lessons.lesson_4_add_layers_and_factories.exceptions.not_found_exception;

public abstract class DataNotFoundException extends Exception {
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
