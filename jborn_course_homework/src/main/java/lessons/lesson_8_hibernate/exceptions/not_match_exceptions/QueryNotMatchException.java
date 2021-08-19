package lessons.lesson_8_hibernate.exceptions.not_match_exceptions;

import lessons.lesson_8_hibernate.exceptions.not_match_exceptions.NotMatchException;

public class QueryNotMatchException extends NotMatchException {
    public QueryNotMatchException() {
        super("Query does not match");
    }

    public QueryNotMatchException(String msg) {
        super(msg);
    }
}
