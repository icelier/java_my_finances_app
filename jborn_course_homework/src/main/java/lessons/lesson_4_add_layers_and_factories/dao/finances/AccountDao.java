package lessons.lesson_4_add_layers_and_factories.dao.finances;

import lessons.lesson_4_add_layers_and_factories.entities.finances.Account;
import lessons.lesson_4_add_layers_and_factories.entities.finances.AccountType;
import lessons.lesson_4_add_layers_and_factories.entities.finances.Operation;
import lessons.lesson_4_add_layers_and_factories.entities.users.UserEntity;
import lessons.lesson_4_add_layers_and_factories.dao.Dao;
import lessons.lesson_4_add_layers_and_factories.singleton_factories.DaoFactory;
import lessons.lesson_5_spring.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_5_spring.exceptions.not_match_exceptions.AccountNotMatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
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
    public Account findById(Long id) throws Exception {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM accounts WHERE id=?")
            ) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            Account account = null;
            if (rs.next()) {
                account = getAccountFromResult(rs);
            }

            return account;
        }
    }

    public Account findById(Long id, Connection connection) throws Exception {
        try(PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM accounts WHERE id=?")
        ) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            Account account = null;
            if (rs.next()) {
                account = getAccountFromResult(rs);
            }

            return account;
        }
    }

    @Override
    public List<Account> findAll() throws Exception {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM accounts"
             )) {

            ResultSet rs = ps.executeQuery();
            List<Account> accounts = new ArrayList<>();
            while (rs.next()) {
                Account account = getAccountFromResult(rs);
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
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO accounts (type_id, user_id, name, total) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setLong(1, account.getType().getId());
                ps.setLong(2, account.getUser().getId());
                ps.setString(3, account.getName());
                ps.setBigDecimal(4, account.getSum());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to insert new account");
                }
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    account.setId(generatedKeys.getLong(1));
                    logger.debug("Account inserted: " + account.getId() +
                            " " + account.getName());
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

    public Account update(Account account, Connection connection) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM accounts WHERE id=?"
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

    private Account getAccountFromResult(ResultSet result) throws Exception {
        Account account = new Account();
        logger.debug("Account found from db: " + result.getLong("id") +
                 " " + result.getString("name"));
        account.setId(result.getLong("id"));
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

    public List<Account> findAllByUserId(Long userId) throws Exception {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM accounts WHERE user_id=?"
             )) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Account> accounts = new ArrayList<>();
            while (rs.next()) {
                Account account = getAccountFromResult(rs);
                accounts.add(account);
            }

            return accounts;
        }
    }

    public void updateSum(Account account, BigDecimal sum, Connection connection, Operation operation) throws Exception {
        try(PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM accounts WHERE id=?",
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE
        )) {

            ps.setLong(1, account.getId());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new AccountNotFoundException("Account for total update not found");
            }
            if (!account.getUser().getId().equals(rs.getLong("user_id")) ||
                    !account.getName().equalsIgnoreCase(rs.getString("name")) ||
                    !account.getType().getId().equals(rs.getLong("type_id")) ||
                    !account.getSum().equals(rs.getBigDecimal("total")))
            {
                throw new lessons.lesson_5_spring.exceptions.not_match_exceptions.AccountNotMatchException("Provided account state does not match currently persisted state");
            }
            logger.debug("Current sum at account: " + account.getSum());
            logger.debug("sum at account in db: " + account.getSum());
            BigDecimal total = rs.getBigDecimal("total");
            if (operation == Operation.CREDIT && (total.compareTo(sum) < 0)) {
                throw new AccountNotMatchException("Transaction sum is beyond current account total");
            }
            if (operation == Operation.CREDIT) {
                rs.updateBigDecimal("total", total.subtract(sum));
            } else {
                rs.updateBigDecimal("total", total.add(sum));
            }

            rs.updateRow();
        }
    }
}
