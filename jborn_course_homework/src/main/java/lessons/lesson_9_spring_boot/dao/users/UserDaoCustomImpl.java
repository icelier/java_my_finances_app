package lessons.lesson_9_spring_boot.dao.users;

import lessons.lesson_9_spring_boot.entities.users.UserEntity;
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
