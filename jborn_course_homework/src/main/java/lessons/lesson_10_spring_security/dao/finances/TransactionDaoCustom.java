package lessons.lesson_10_spring_security.dao.finances;

import lessons.lesson_10_spring_security.entities.finances.Transaction;

public interface TransactionDaoCustom {
    void detach(Transaction transaction);
}
