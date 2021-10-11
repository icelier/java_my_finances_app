package com.chalova.irina.myfinances.users_service.exceptions;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException() {
        super("No such role in the database");
    }

    public RoleNotFoundException(String msg) {
        super(msg);
    }
}
