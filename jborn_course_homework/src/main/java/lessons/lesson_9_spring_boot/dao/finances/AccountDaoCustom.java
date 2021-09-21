package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.Account;

public interface AccountDaoCustom {
    void detach(Account account);
}
