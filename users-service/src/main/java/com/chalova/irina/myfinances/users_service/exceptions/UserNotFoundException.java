package com.chalova.irina.myfinances.users_service.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("No such user in the database");
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }
}
