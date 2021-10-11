package com.chalova.irina.myfinances.finance_service.exceptions.accounts;

import com.chalova.irina.myfinances.commons_service.exceptions.not_found_exception.DataNotFoundException;

public class AccountNotFoundException extends DataNotFoundException {
    public AccountNotFoundException() {
        super("No such account in the database");
    }

    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
