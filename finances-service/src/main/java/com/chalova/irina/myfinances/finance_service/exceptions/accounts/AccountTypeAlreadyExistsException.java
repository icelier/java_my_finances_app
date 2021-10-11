package com.chalova.irina.myfinances.finance_service.exceptions.accounts;

import com.chalova.irina.myfinances.commons_service.exceptions.already_exists_exception.DataAlreadyExistsException;

public class AccountTypeAlreadyExistsException extends DataAlreadyExistsException {
    public AccountTypeAlreadyExistsException() {
        super("Account type already exists in the database");
    }

    public AccountTypeAlreadyExistsException(String msg) {
        super(msg);
    }
}
