package lessons.lesson_9_spring_boot.services.finances;

import lessons.lesson_9_spring_boot.dao.finances.CategoryDao;
import lessons.lesson_9_spring_boot.entities.finances.Category;
import lessons.lesson_9_spring_boot.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService implements AbstractService<Category, Long> {
    @Autowired
    private final CategoryDao categoryDao;

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public Category findByTitle(String title) {
        return categoryDao.findByTitle(title);
    }

    @Override
    public Category findById(Long id) {
        return categoryDao.findById(id).orElse(null);
    }

    @Override
    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    @Transactional
    @Override
    public Category insert(Category category) {
        category =  categoryDao.save(category);

        return category;
    }

    @Transactional
    @Override
    public Category update(Category category) {
        category =  categoryDao.save(category);

        return category;
    }

    @Transactional
    @Override
    public void delete(Category category) {
        categoryDao.delete(category);
    }

    @Transactional
    @Override
    public void deleteAll() {
        categoryDao.deleteAll();
    }
}
