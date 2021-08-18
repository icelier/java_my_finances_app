package lessons.lesson_7_controllers.exceptions.not_match_exceptions;

public class QueryNotMatchException extends NotMatchException {
    public QueryNotMatchException() {
        super("Query does not match");
    }

    public QueryNotMatchException(String msg) {
        super(msg);
    }
}
