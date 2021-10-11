package com.chalova.irina.myfinances.finance_service.exceptions.accounts;

import com.chalova.irina.myfinances.commons_service.exceptions.not_match_exceptions.NotMatchException;

public class AccountNotMatchException extends NotMatchException {
    public AccountNotMatchException() {
        super("Account state does not match");
    }

    public AccountNotMatchException(String msg) {
        super(msg);
    }
}
