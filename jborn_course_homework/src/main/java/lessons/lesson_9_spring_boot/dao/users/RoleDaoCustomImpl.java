package lessons.lesson_9_spring_boot.dao.users;

import lessons.lesson_9_spring_boot.entities.users.Role;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class RoleDaoCustomImpl implements RoleDaoCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detach(Role role) {
        entityManager.detach(role);
    }
}
