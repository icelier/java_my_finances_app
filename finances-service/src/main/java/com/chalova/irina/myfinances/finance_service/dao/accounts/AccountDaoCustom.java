package com.chalova.irina.myfinances.finance_service.dao.accounts;

import com.chalova.irina.myfinances.finance_service.entities.accounts.Account;

public interface AccountDaoCustom {
    void detach(Account account);
}
