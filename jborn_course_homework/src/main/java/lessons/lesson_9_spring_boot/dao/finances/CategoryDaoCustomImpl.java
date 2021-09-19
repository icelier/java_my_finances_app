package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.Category;
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
