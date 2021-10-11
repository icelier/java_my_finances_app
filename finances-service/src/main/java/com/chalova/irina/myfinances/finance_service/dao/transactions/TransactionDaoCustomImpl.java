package com.chalova.irina.myfinances.finance_service.dao.transactions;

import com.chalova.irina.myfinances.finance_service.entities.transactions.AccountTransaction;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class TransactionDaoCustomImpl implements TransactionDaoCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detach(AccountTransaction transaction) {
        entityManager.detach(transaction);
    }
}
