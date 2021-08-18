package lessons.lesson_5_spring.services.finances;

import lessons.lesson_5_spring.dao.finances.CategoryDao;
import lessons.lesson_5_spring.entities.finances.Category;
import lessons.lesson_5_spring.exceptions.already_exists_exception.CategoryAlreadyExistsException;
import lessons.lesson_5_spring.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_5_spring.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_5_spring.services.AbstractService;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CategoryService implements AbstractService<Category, Long> {
    private final CategoryDao categoryDao;

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public Category getByTitle(String title) throws SQLException {
        return categoryDao.findByTitle(title);
    }

    @Override
    public Category findById(Long id) throws SQLException {
        return categoryDao.findById(id);
    }

    @Override
    public List<Category> findAll() throws SQLException {
        return categoryDao.findAll();
    }

    @Override
    public Category insert(Category category) throws SQLException, OperationFailedException, CategoryAlreadyExistsException {
        return categoryDao.insert(category);
    }

    @Override
    public Category update(Category category) throws SQLException, CategoryNotFoundException, OperationFailedException {
        return categoryDao.update(category);
    }

    @Override
    public void delete(Category category) throws SQLException, OperationFailedException {
        categoryDao.delete(category);
    }
}
