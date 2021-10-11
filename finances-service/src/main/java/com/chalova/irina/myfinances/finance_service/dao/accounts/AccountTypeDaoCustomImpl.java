package com.chalova.irina.myfinances.finance_service.dao.accounts;

import com.chalova.irina.myfinances.finance_service.entities.accounts.AccountType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AccountTypeDaoCustomImpl implements AccountTypeDaoCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detach(AccountType accountType) {
        entityManager.detach(accountType);
    }
}
