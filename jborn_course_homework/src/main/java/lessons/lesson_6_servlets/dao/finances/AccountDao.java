package lessons.lesson_6_servlets.dao.finances;

import lessons.lesson_6_servlets.dao.AbstractDao;
import lessons.lesson_6_servlets.dao.users.UserDao;
import lessons.lesson_6_servlets.entities.finances.Account;
import lessons.lesson_6_servlets.entities.finances.AccountType;
import lessons.lesson_6_servlets.entities.finances.Operation;
import lessons.lesson_6_servlets.entities.users.UserEntity;
import lessons.lesson_6_servlets.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_6_servlets.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_6_servlets.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_6_servlets.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_6_servlets.exceptions.operation_failed.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AccountDao extends AbstractDao<Account, Long> {
    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    private final DataSource dataSource;
    private final UserDao userDao;
    private final AccountTypeDao accountTypeDao;

    public AccountDao(DataSource dataSource, UserDao userDao, AccountTypeDao accountTypeDao) {
        super(dataSource);
        this.dataSource = dataSource;
        this.userDao = userDao;
        this.accountTypeDao = accountTypeDao;
    }

    @Override
    public Account findById(Long id) throws SQLException {
        return executeFindByIdQuery(id);
    }

    public Account findById(Long id, Connection connection) throws SQLException {
        return executeFindByIdQuery(connection, id);
    }

    @Override
    public List<Account> findAll() throws SQLException {
        return executeFindAllQuery();
    }

    /**
     * Returns inserted account entity with new id generated. First checks for entity presence in the database
     * based on equals method criteria
     * @param account entity to get parameters for insert query
     * @return inserted account with generated id
     * @throws SQLException if database access error occurred, if query parameter is incorrect
     * @throws OperationFailedException if id generation for inserted account failed
     * @throws AccountAlreadyExistsException if account found in the database by equals method criteria
     */
    @Override
    public Account insert(Account account) throws SQLException, OperationFailedException, AccountAlreadyExistsException {
        boolean alreadyExists = checkIfAlreadyExists(account);
        if (alreadyExists) {
            throw new AccountAlreadyExistsException("Account already exists");
        }

        return executeInsertQuery(account);
    }

    /**
     * Returns updated account
     * @return updated account if success
     */
    @Override
    public Account update(Account account) throws SQLException, AccountNotFoundException, OperationFailedException {
        try {
            executeUpdateQuery(account.getId(), account);
        } catch (DataNotFoundException e) {
            throw  new AccountNotFoundException("Account not found in the database");
        }

        return account;
    }

    public Account update(Account account, Connection connection) throws SQLException, AccountNotFoundException, OperationFailedException {
        try {
            executeUpdateQuery(connection, account.getId(), account);
        } catch (DataNotFoundException e) {
            throw  new AccountNotFoundException("Account " + account.getName() + " not found in the database");
        }

        return account;
    }

    @Override
    public void delete(Account account) throws SQLException, OperationFailedException {
        executeDeleteQuery(account);
    }

    public List<Account> findAllByUserId(Long userId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     getFindByUserIdQuery()
             )) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Account> accounts = new ArrayList<>();
            while (rs.next()) {
                Account account = getDomainFromQueryResult(rs);
                accounts.add(account);
            }

            return accounts;
        }
    }

    /**
     * Updates total for the given account id with the given sum based on operation type. If operation type is credit, sum is subtracted and
     * if debet then sum is added
     * @param accountId account id where sum to be updated
     * @param sum to be subtracted or added from/to the given account based on operation type
     * @param connection to execute query
     * @param operation type for operation, either credit or debet
     * @throws AccountNotFoundException if account not found in database by id
     * @throws AccountNotMatchException if there is not enough money at the account found in database
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     */
    public void updateSum(Long accountId, BigDecimal sum, Connection connection, Operation operation) throws AccountNotFoundException, AccountNotMatchException, SQLException {
        try(PreparedStatement ps = connection.prepareStatement(
                getFindByIdQuery(),
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE
        )) {

            setupFindByIdQuery(ps, accountId);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new AccountNotFoundException("Account for total update not found");
            }

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

    @Override
    protected String getInsertQuery() {
        return "INSERT INTO accounts (type_id, user_id, name, total) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String getFindByIdQuery() {
        return "SELECT * FROM accounts WHERE id=?";
    }

    private String getFindByUserIdQuery() {
        return "SELECT * FROM accounts WHERE user_id=?";
    }

    @Override
    protected String getFindDomainQuery() {
        return "SELECT * FROM accounts WHERE type_id=? AND user_id=? AND name=?";
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT * FROM accounts ORDER BY id ASC";
    }

    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM accounts WHERE id=?";
    }

    @Override
    public void setupFindByIdQuery(PreparedStatement ps, Long id) throws SQLException {
        ps.setLong(1, id);
    }

    @Override
    public void setupInsertQuery(PreparedStatement ps, Account account) throws SQLException {
        ps.setLong(1, account.getType().getId());
        ps.setLong(2, account.getUser().getId());
        ps.setString(3, account.getName());
        ps.setBigDecimal(4, account.getSum());
    }

    @Override
    public void setupDeleteQuery(PreparedStatement ps, Account account) throws SQLException {
        ps.setLong(1, account.getId());
    }

    @Override
    public void setupFindDomainQuery(PreparedStatement ps, Account account) throws SQLException {
        ps.setLong(1, account.getType().getId());
        ps.setLong(2, account.getUser().getId());
        ps.setString(3, account.getName());
    }

    @Override
    public Account getDomainFromQueryResult(ResultSet result) throws SQLException {
        Account account = new Account();
        logger.debug("Account found from db: " + result.getLong("id") +
                " " + result.getString("name"));
        account.setId(result.getLong("id"));
        account.setName(result.getString("name"));
        account.setSum(result.getBigDecimal("total"));
        Long typeId = result.getLong("type_id");
        AccountType accountType = accountTypeDao.findById(typeId);
        account.setType(accountType);
        Long userId = result.getLong("user_id");
        UserEntity user = userDao.findById(userId);
        account.setUser(user);

        return account;
    }

    @Override
    public void updateDomain(ResultSet rs, Account account) throws SQLException {
        rs.updateLong("type_id", account.getType().getId());
        rs.updateLong("user_id", account.getUser().getId());
        rs.updateString("name", account.getName());
        rs.updateBigDecimal("total", account.getSum());
        rs.updateRow();
    }
}
