package lessons.lesson_4_add_layers_and_factories.services.finances;

import lessons.lesson_4_add_layers_and_factories.dao.finances.CategoryDao;
import lessons.lesson_4_add_layers_and_factories.entities.finances.Category;
import lessons.lesson_4_add_layers_and_factories.services.Service;

import java.sql.SQLException;
import java.util.List;

public class CategoryService implements Service<Category, Long> {
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
    public Category insert(Category category) throws SQLException {
        return categoryDao.insert(category);
    }

    @Override
    public Category update(Category category) throws SQLException {
        return categoryDao.update(category);
    }

    @Override
    public boolean delete(Category category) throws SQLException {
        return categoryDao.delete(category);
    }
}
