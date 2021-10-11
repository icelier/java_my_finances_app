package com.chalova.irina.myfinances.finance_service.dao.transactions;

import com.chalova.irina.myfinances.finance_service.entities.transactions.Category;

public interface CategoryDaoCustom {
    void detach(Category category);
}
