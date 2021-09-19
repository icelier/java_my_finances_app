package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.Transaction;

public interface TransactionDaoCustom {
    void detach(Transaction transaction);
}
