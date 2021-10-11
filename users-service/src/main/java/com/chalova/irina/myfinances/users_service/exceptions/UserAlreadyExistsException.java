package com.chalova.irina.myfinances.users_service.exceptions;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException() {
        super("User already exists in the database");
    }

    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
