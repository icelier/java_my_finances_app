package lessons.lesson_4.dao.finances;

import lessons.lesson_4.entities.finances.AccountType;
import lessons.lesson_4.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountTypeDao implements Dao<AccountType, Long> {
    private static final Logger logger = LoggerFactory.getLogger(AccountTypeDao.class);
    private final DataSource dataSource;

    public AccountTypeDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public AccountType findById(Long id) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM account_types WHERE id=?")
            ) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            AccountType type = null;
            if (rs.next()) {
                type = getAccountTypeFromResult(rs, id);
            }

            return type;
        }
    }

    /**
     * Returns list of account types
     * @return list of account types or empty list if no any
     * @throws SQLException
     */
    @Override
    public List<AccountType> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM account_types"
             )) {

            ResultSet rs = ps.executeQuery();
            List<AccountType> types = new ArrayList<>();
            while (rs.next()) {
                AccountType type = getAccountTypeFromResult(rs, rs.getLong("id"));
                types.add(type);
            }

            return types;
        }
    }

    /**
     * Returns inserted account or null if account already exists
     * @return inserted account if success
     */
    @Override
    public AccountType insert(AccountType type) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO account_types (title) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
            ps.setString(1, type.getTitle());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to insert new account type");
            }
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                type.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("Failed to insert new account type, no ID obtained");
            }

            return type;
        }
    }

    /**
     * Returns updated account type or null if account type does not exist
     * @return updated account type if success
     */
    @Override
    public AccountType update(AccountType type) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM account_types WHERE id=?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            )) {

            ps.setLong(1, type.getId());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return  null;
            } else {
                rs.updateString("title", type.getTitle());
                rs.updateRow();
            }

            return type;
        }
    }

    /**
     * Returns true if account type deleted else false
     * @return true if account type deleted
     */
    @Override
    public boolean delete(AccountType type) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM account_types WHERE id=?"
            )) {

            ps.setLong(1, type.getId());

            int deletedRowsCount = ps.executeUpdate();
            if (deletedRowsCount <= 0) {
                return false;
            }

            return true;
        }
    }

    private AccountType getAccountTypeFromResult(ResultSet result, Long id) throws SQLException {
        AccountType type = new AccountType();
        logger.debug("AccountType found from db: " + result.getString("title"));
        type.setId(id);
        type.setTitle(result.getString("title"));

        return type;
    }
}
