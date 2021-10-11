package com.chalova.irina.myfinances.finance_service.exceptions.transactions;

import com.chalova.irina.myfinances.commons_service.exceptions.already_exists_exception.DataAlreadyExistsException;

public class CategoryAlreadyExistsException extends DataAlreadyExistsException {
    public CategoryAlreadyExistsException() {
        super("Trasaction category already exists in the database");
    }

    public CategoryAlreadyExistsException(String msg) {
        super(msg);
    }
}
