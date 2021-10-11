package com.chalova.irina.myfinances.finance_service.exceptions.accounts;

import com.chalova.irina.myfinances.commons_service.exceptions.already_exists_exception.DataAlreadyExistsException;

public class AccountAlreadyExistsException extends DataAlreadyExistsException {
    public AccountAlreadyExistsException() {
        super("Account already exists in the database");
    }

    public AccountAlreadyExistsException(String msg) {
        super(msg);
    }
}
