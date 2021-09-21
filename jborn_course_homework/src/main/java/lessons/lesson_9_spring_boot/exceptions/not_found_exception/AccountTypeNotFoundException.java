package lessons.lesson_9_spring_boot.exceptions.not_found_exception;

import lessons.lesson_9_spring_boot.exceptions.not_found_exception.DataNotFoundException;

public class AccountTypeNotFoundException extends DataNotFoundException {
    public AccountTypeNotFoundException() {
        super("No such account type in the database");
    }

    public AccountTypeNotFoundException(String msg) {
        super(msg);
    }
}
