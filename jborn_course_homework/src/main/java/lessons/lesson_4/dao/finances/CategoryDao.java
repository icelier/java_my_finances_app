package lessons.lesson_4.dao.finances;

import lessons.lesson_4.entities.finances.Category;
import lessons.lesson_4.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao implements Dao<Category, Long> {
    private static final Logger logger = LoggerFactory.getLogger(CategoryDao.class);

    private final DataSource dataSource;

    public CategoryDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Category findById(Long id) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM categories WHERE id=?"
            )) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            Category category = null;
            if (rs.next()) {
                category = getCategoryFromResult(rs, id);
            }

            return category;
        }
    }

    /**
     * Returns list of categories
     * @return list of categories or empty list if no any
     * @throws SQLException
     */
    @Override
    public List<Category> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM categories"
             )) {

            ResultSet rs = ps.executeQuery();
            List<Category> categories = new ArrayList<>();
            while (rs.next()) {
                Category category = getCategoryFromResult(rs, rs.getLong("id"));
                categories.add(category);
            }

            return categories;
        }
    }

    /**
     * Returns inserted category or null if category already exists
     * @return inserted category if success
     */
    @Override
    public Category insert(Category category) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO categories (title) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
            ps.setString(1, category.getTitle());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to insert new category");
            }
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                category.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("Failed to insert new category, no ID obtained");
            }

            return category;
        }
    }

    /**
     * Returns updated category or null if category does not exist
     * @return updated category if success
     */
    @Override
    public Category update(Category category) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM categories WHERE id=?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            )) {

            ps.setLong(1, category.getId());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return  null;
            } else {
                rs.updateString("title", category.getTitle());
                rs.updateRow();
            }

            return category;
        }
    }

    /**
     * Returns true if category deleted else false
     * @return true if category deleted
     */
    @Override
    public boolean delete(Category category) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM categories WHERE id=?"
            )) {

            ps.setLong(1, category.getId());

            int deletedRowsCount = ps.executeUpdate();
            if (deletedRowsCount <= 0) {
                return false;
            }

            return true;
        }
    }

    private Category getCategoryFromResult(ResultSet result, Long id) throws SQLException {
        Category category = new Category();
        logger.debug("Category found from db: " + result.getString("title"));
        category.setId(id);
        category.setTitle(result.getString("title"));

        return category;
    }
}
