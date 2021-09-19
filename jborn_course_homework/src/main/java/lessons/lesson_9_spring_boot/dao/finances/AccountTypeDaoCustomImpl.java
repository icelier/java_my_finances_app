package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.AccountType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AccountTypeDaoCustomImpl implements AccountTypeDaoCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detach(AccountType accountType) {
        entityManager.detach(accountType);
    }
}
