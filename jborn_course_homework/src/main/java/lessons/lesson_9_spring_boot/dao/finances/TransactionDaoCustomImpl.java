package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.Transaction;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class TransactionDaoCustomImpl implements TransactionDaoCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detach(Transaction transaction) {
        entityManager.detach(transaction);
    }
}
