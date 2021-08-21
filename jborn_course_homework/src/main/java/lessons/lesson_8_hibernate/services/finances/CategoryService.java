package lessons.lesson_8_hibernate.services.finances;

import lessons.lesson_8_hibernate.dao.finances.CategoryDao;
import lessons.lesson_8_hibernate.entities.finances.Category;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.CategoryAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_8_hibernate.services.AbstractService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryService extends AbstractService<Category, Long> {
    private final CategoryDao categoryDao;

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public Category findByTitle(String title) throws OperationFailedException, CategoryNotFoundException {
        return categoryDao.findByTitle(title);
    }

    @Override
    public Category findById(Long id) throws OperationFailedException {
        return categoryDao.findById(id);
    }

    @Override
    public List<Category> findAll() throws OperationFailedException {
        return categoryDao.findAll();
    }

    @Override
    public Category insert(Category category) throws OperationFailedException, CategoryAlreadyExistsException {
        return categoryDao.insert(category);
    }

    @Override
    public Category update(Category category) throws CategoryNotFoundException, OperationFailedException {
        return categoryDao.update(category);
    }

    @Override
    public void delete(Category category) throws OperationFailedException, DataNotFoundException {
        categoryDao.delete(category);
    }

    @Override
    public int deleteAll() throws OperationFailedException {
        return categoryDao.deleteAll();
    }
}
