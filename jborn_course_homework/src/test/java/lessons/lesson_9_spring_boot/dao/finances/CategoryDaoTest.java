package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.Category;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class CategoryDaoTest {
    @Autowired private CategoryDao subj;
    @Autowired private TransactionDao transactionDao;

    private Category category;

    @Before
    public void setUp() throws Exception {
        category = new Category("new category");
        subj.save(category);
        subj.detach(category);
    }

    @Test
    public void findByTitle_ok() {
        assertNotNull(subj.findByTitle("new category").orElse(null));
    }

    @Test
    public void findByTitle_passUnknownTitle_returnNull() {
        assertNull(subj.findByTitle("unknown category").orElse(null));
    }

    @Test
    public void updateCategoryById_ok() {
        subj.updateCategoryById("new title", category.getId());

        assertEquals("new title", subj.getById(category.getId()).getTitle());
    }

    @Test
    public void deleteCategoryById_ok() {
        subj.deleteCategoryById(category.getId());

        assertNull(subj.findById(category.getId()).orElse(null));
    }

    @Test
    public void deleteAllCategories_ok() {
        // to eliminate ConstraintViolationException
        transactionDao.deleteAllTransactions();

        subj.deleteAllCategories();

        assertEquals(0, subj.findAll().size());
    }
}