package lessons.lesson_6_servlets.exceptions.not_match_exceptions;

public class QueryNotMatchException extends NotMatchException {
    public QueryNotMatchException() {
        super("Query does not match");
    }

    public QueryNotMatchException(String msg) {
        super(msg);
    }
}
