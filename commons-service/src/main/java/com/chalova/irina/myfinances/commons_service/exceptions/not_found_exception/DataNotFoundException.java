package com.chalova.irina.myfinances.commons_service.exceptions.not_found_exception;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String msg) {
        super(msg);
    }
}
