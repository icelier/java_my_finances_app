package com.chalova.irina.myfinances.finance_service.exceptions.transactions;

import com.chalova.irina.myfinances.commons_service.exceptions.already_exists_exception.DataAlreadyExistsException;

public class TransactionAlreadyExistsException extends DataAlreadyExistsException {
    public TransactionAlreadyExistsException() {
        super("Transaction already exists in the database");
    }

    public TransactionAlreadyExistsException(String msg) {
        super(msg);
    }
}
