package com.chalova.irina.myfinances.finance_service.dao.accounts;

import com.chalova.irina.myfinances.finance_service.entities.accounts.Account;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AccountDaoCustomImpl implements AccountDaoCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detach(Account account) {
        entityManager.detach(account);
    }
}
