package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.AccountType;

public interface AccountTypeDaoCustom {
    void detach(AccountType accountType);
}
