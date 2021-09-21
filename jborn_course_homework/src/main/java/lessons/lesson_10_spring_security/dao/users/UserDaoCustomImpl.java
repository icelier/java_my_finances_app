package lessons.lesson_10_spring_security.dao.users;

import lessons.lesson_10_spring_security.entities.users.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserDaoCustomImpl implements UserDaoCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detach(UserEntity user) {
        entityManager.detach(user);
    }
}
