package com.chalova.irina.myfinances.users_service.exceptions;

public class RoleAlreadyExistsException extends Exception {
    public RoleAlreadyExistsException() {
        super("Role already exists in the database");
    }

    public RoleAlreadyExistsException(String msg) {
        super(msg);
    }
}
