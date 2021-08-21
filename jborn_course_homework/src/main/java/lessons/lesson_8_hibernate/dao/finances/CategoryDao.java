package lessons.lesson_8_hibernate.dao.finances;

import lessons.lesson_8_hibernate.dao.AbstractDao;
import lessons.lesson_8_hibernate.entities.finances.Category;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.CategoryAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.DataAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;

@Repository
public class CategoryDao extends AbstractDao<Category, Long> {
    private static final Logger logger = LoggerFactory.getLogger(CategoryDao.class);

    private final EntityManager entityManager;

    public CategoryDao(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Category findById(Long id) throws OperationFailedException {
        Category category;
        try {
            category = entityManager.find(Category.class, id);
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return category;
    }

    public Category findByTitle(String title) throws CategoryNotFoundException, OperationFailedException {
        Category category;
        try {
            Query query = entityManager.createQuery(getFindByTitleQuery(), Category.class);
            query.setParameter("title", title);
            category = (Category) query.getSingleResult();
        } catch (NoResultException e) {
            throw new CategoryNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return category;
    }

    @Override
    public List<Category> findAll() throws OperationFailedException {
        List<Category> categories;
        try {
            TypedQuery<Category> query = entityManager.createQuery(getFindAllQuery(), Category.class);
            categories = query.getResultList();
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return categories;
    }

    @Override
    public Category insert(Category category) throws OperationFailedException, CategoryAlreadyExistsException {
        try {
            super.insert(category);
        } catch (DataAlreadyExistsException e) {
            throw new CategoryAlreadyExistsException(e.getMessage());
        }

        return category;
    }

    @Override
    public Category update(Category category) throws CategoryNotFoundException, OperationFailedException {
        entityManager.getTransaction().begin();
        Category categoryFromDb = findById(category.getId());
        if (categoryFromDb == null) {
            throw new CategoryNotFoundException("Transaction category " + category.getTitle() + " not found in the database");
        }
        categoryFromDb = executeUpdateQuery(categoryFromDb, category);
        entityManager.getTransaction().commit();

        return categoryFromDb;
    }

    @Override
    public void delete(Category category) throws OperationFailedException, DataNotFoundException {
        Category categoryFromDb = findById(category.getId());
        if (categoryFromDb == null) {
            throw new CategoryNotFoundException("Transaction category " + category.getTitle() + " not found in the database");
        }
        super.delete(category);
    }

    @Override
    public int deleteAll() throws OperationFailedException {
        List<Category> categories = findAll();
        int deletedRows = 0;
        if (!categories.isEmpty()) {
            deletedRows = super.deleteAll();
        }

        return deletedRows;
    }

    @Override
    protected String getFindByIdQuery() {
        return "SELECT cat FROM Category cat WHERE cat.id=:id";
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT cat FROM Category cat ORDER BY cat.id ASC";
    }

    private String getFindByTitleQuery() {
        return "SELECT cat FROM Category cat WHERE cat.title=:title";
    }

    @Override
    protected String getDeleteAllQuery() {
        return "DELETE FROM Category cat";
    }

    @Override
    public void updateDomain(Category persistentCategory, Category category) {
        category.setTitle(category.getTitle());
    }
}
