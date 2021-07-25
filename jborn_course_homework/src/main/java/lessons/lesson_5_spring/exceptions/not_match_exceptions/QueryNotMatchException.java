package lessons.lesson_5_spring.exceptions.not_match_exceptions;

public class QueryNotMatchException extends NotMatchException {
    public QueryNotMatchException() {
        super("Query does not match");
    }

    public QueryNotMatchException(String msg) {
        super(msg);
    }
}
