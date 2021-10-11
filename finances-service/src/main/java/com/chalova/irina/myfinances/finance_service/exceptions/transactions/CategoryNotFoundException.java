package com.chalova.irina.myfinances.finance_service.exceptions.transactions;

import com.chalova.irina.myfinances.commons_service.exceptions.not_found_exception.DataNotFoundException;

public class CategoryNotFoundException extends DataNotFoundException {
    public CategoryNotFoundException() {
        super("No such transaction category in the database");
    }

    public CategoryNotFoundException(String msg) {
        super(msg);
    }
}
