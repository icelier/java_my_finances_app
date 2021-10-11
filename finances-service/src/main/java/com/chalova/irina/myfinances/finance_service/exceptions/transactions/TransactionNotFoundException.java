package com.chalova.irina.myfinances.finance_service.exceptions.transactions;

import com.chalova.irina.myfinances.commons_service.exceptions.not_found_exception.DataNotFoundException;

public class TransactionNotFoundException extends DataNotFoundException {
    public TransactionNotFoundException() {
        super("No such transaction in the database");
    }

    public TransactionNotFoundException(String msg) {
        super(msg);
    }
}
