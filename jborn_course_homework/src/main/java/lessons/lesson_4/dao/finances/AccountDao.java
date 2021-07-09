package lessons.lesson_4.dao.finances;

import lessons.lesson_4.entities.finances.Account;
import lessons.lesson_4.entities.finances.AccountType;
import lessons.lesson_4.entities.users.UserEntity;
import lessons.lesson_4.dao.Dao;
import lessons.lesson_4.singleton_factories.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDao implements Dao<Account, Long> {
    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    private final DataSource dataSource;

    public AccountDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Account findById(Long id) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM accounts WHERE id=?")
            ) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            Account account = null;
            if (rs.next()) {
                account = getAccountFromResult(rs, id);
            }

            return account;
        }
    }

    @Override
    public List<Account> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM accounts"
             )) {

            ResultSet rs = ps.executeQuery();
            List<Account> accounts = new ArrayList<>();
            while (rs.next()) {
                Account account = getAccountFromResult(rs, rs.getLong("id"));
                accounts.add(account);
            }

            return accounts;
        }
    }

    /**
     * Returns inserted account or null if account already exists
     * @return inserted account if success
     */
    @Override
    public Account insert(Account account) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO accounts (type_id, user_id, name, total) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                preparedStatement.setLong(1, account.getType().getId());
                preparedStatement.setLong(2, account.getUser().getId());
                preparedStatement.setString(3, account.getName());
                preparedStatement.setBigDecimal(4, account.getSum());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to insert new account");
                }
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    account.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Failed to insert new account, no ID obtained");
                }

            return account;
        }
    }

    /**
     * Returns updated account or null if account does not exist
     * @return updated account if success
     */
    @Override
    public Account update(Account account) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM accounts WHERE id=?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            )) {

            ps.setLong(1, account.getId());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return  null;
            } else {
                rs.updateLong("type_id", account.getType().getId());
                rs.updateLong("user_id", account.getUser().getId());
                rs.updateString("name", account.getName());
                rs.updateBigDecimal("total", account.getSum());
                rs.updateRow();
            }

            return account;
        }
    }

    /**
     * Returns true if account deleted else false
     * @return true if account deleted
     */
    @Override
    public boolean delete(Account account) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM accounts WHERE id=?"
            )) {

            ps.setLong(1, account.getId());

            int deletedRowsCount = ps.executeUpdate();
            if (deletedRowsCount <= 0) {
                return false;
            }

            return true;
        }
    }

    private Account getAccountFromResult(ResultSet result, Long id) throws SQLException {
        Account account = new Account();
        logger.debug("Account found from db: " + result.getString("name"));
        account.setId(id);
        account.setName(result.getString("name"));
        account.setSum(result.getBigDecimal("total"));
        Long typeId = result.getLong("type_id");
        AccountType accountType = DaoFactory.getAccountTypeDao().findById(typeId);
        account.setType(accountType);
        Long userId = result.getLong("user_id");
        UserEntity user = DaoFactory.getUserDao().findById(userId);
        account.setUser(user);

        return account;
    }
 }
