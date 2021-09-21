package lessons.lesson_10_spring_security.dao.finances;

import lessons.lesson_10_spring_security.entities.finances.Account;

public interface AccountDaoCustom {
    void detach(Account account);
}
