package lessons.lesson_10_spring_security.dao.finances;

import lessons.lesson_10_spring_security.entities.finances.Category;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class CategoryDaoCustomImpl implements CategoryDaoCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detach(Category category) {
        entityManager.detach(category);
    }
}
