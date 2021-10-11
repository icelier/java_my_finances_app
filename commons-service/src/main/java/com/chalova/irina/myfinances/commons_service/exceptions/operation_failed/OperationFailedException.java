package com.chalova.irina.myfinances.commons_service.exceptions.operation_failed;

public class OperationFailedException extends RuntimeException {
    public OperationFailedException(String msg) {
        super(msg);
    }
}
