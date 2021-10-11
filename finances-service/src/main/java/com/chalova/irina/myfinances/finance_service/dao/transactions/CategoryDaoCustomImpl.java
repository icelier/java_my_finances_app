package com.chalova.irina.myfinances.finance_service.dao.transactions;

import com.chalova.irina.myfinances.finance_service.entities.transactions.Category;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class CategoryDaoCustomImpl implements CategoryDaoCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detach(Category category) {
        entityManager.detach(category);
    }
}
