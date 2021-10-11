package com.chalova.irina.myfinances.finance_service.dao.transactions;

import com.chalova.irina.myfinances.finance_service.entities.transactions.AccountTransaction;

public interface TransactionDaoCustom {
    void detach(AccountTransaction transaction);
}
