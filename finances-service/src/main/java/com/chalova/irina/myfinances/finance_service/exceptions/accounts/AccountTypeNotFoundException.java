package com.chalova.irina.myfinances.finance_service.exceptions.accounts;

import com.chalova.irina.myfinances.commons_service.exceptions.not_found_exception.DataNotFoundException;

public class AccountTypeNotFoundException extends DataNotFoundException {
    public AccountTypeNotFoundException() {
        super("No such account type in the database");
    }

    public AccountTypeNotFoundException(String msg) {
        super(msg);
    }
}
