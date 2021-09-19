package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.Account;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AccountDaoCustomImpl implements AccountDaoCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detach(Account account) {
        entityManager.detach(account);
    }
}
