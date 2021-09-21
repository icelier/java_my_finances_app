package lessons.lesson_10_spring_security.dao.finances;

import lessons.lesson_10_spring_security.entities.finances.AccountType;

public interface AccountTypeDaoCustom {
    void detach(AccountType accountType);
}
