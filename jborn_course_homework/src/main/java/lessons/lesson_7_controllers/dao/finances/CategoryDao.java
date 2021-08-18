package lessons.lesson_7_controllers.dao.finances;

import lessons.lesson_7_controllers.dao.AbstractDao;
import lessons.lesson_7_controllers.entities.finances.Category;
import lessons.lesson_7_controllers.exceptions.already_exists_exception.CategoryAlreadyExistsException;
import lessons.lesson_7_controllers.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_7_controllers.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_7_controllers.exceptions.operation_failed.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CategoryDao extends AbstractDao<Category, Long> {
    private static final Logger logger = LoggerFactory.getLogger(CategoryDao.class);

    private final DataSource dataSource;

    public CategoryDao(DataSource dataSource) {
        super(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public Category findById(Long id) throws SQLException {
        return executeFindByIdQuery(id);
    }

    public Category findByTitle(String title) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    getFindByTitleQuery()
            )) {

            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
            Category category = null;
            if (rs.next()) {
                category = getDomainFromQueryResult(rs);
            }

            return category;
        }
    }

    /**
     * Returns list of categories
     * @return list of categories or empty list if no any
     */
    @Override
    public List<Category> findAll() throws SQLException {
        return executeFindAllQuery();
    }

    /**
     * Returns inserted category or null if category already exists
     * @return inserted category if success
     */
    @Override
    public Category insert(Category category) throws SQLException, OperationFailedException, CategoryAlreadyExistsException {
        boolean alreadyExists = checkIfAlreadyExists(category);
        if (alreadyExists) {
            throw new CategoryAlreadyExistsException("Transaction category already exists");
        }

        return executeInsertQuery(category);
    }

    /**
     * Returns updated category
     * @return updated category if success
     */
    @Override
    public Category update(Category category) throws SQLException, CategoryNotFoundException, OperationFailedException {
        try {
            executeUpdateQuery(category.getId(), category);
        } catch (DataNotFoundException e) {
            throw  new CategoryNotFoundException("Transaction category not found in the database");
        }

        return category;
    }

    @Override
    public void delete(Category category) throws SQLException, OperationFailedException {
        executeDeleteQuery(category);
    }

    @Override
    protected String getInsertQuery() {
        return "INSERT INTO categories (title) VALUES (?)";
    }

    @Override
    protected String getFindByIdQuery() {
        return "SELECT * FROM categories WHERE id=?";
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT * FROM categories ORDER BY id ASC";
    }

    private String getFindByTitleQuery() {
        return "SELECT * FROM categories WHERE title LIKE ?";
    }

    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM categories WHERE id=?";
    }

    @Override
    protected String getFindDomainQuery() {
        return "SELECT * FROM categories WHERE title=?";
    }

    @Override
    public void setupFindByIdQuery(PreparedStatement ps, Long id) throws SQLException {
        ps.setLong(1, id);
    }

    @Override
    public void setupInsertQuery(PreparedStatement ps, Category category) throws SQLException {
        ps.setString(1, category.getTitle());
    }

    @Override
    public void setupDeleteQuery(PreparedStatement ps, Category category) throws SQLException {
        ps.setLong(1, category.getId());
    }

    @Override
    public void setupFindDomainQuery(PreparedStatement ps, Category category) throws SQLException {
        ps.setString(1, category.getTitle());
    }

    @Override
    public Category getDomainFromQueryResult(ResultSet rs) throws SQLException {
        Category category = new Category();
        logger.debug("Category found from db: " + rs.getString("title"));
        category.setId(rs.getLong("id"));
        category.setTitle(rs.getString("title"));

        return category;
    }

    @Override
    public void updateDomain(ResultSet rs, Category category) throws SQLException, OperationFailedException {
        rs.updateString("title", category.getTitle());
        rs.updateRow();
    }
}
