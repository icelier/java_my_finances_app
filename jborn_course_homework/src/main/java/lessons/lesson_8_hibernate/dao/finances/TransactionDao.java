package lessons.lesson_8_hibernate.dao.finances;

import lessons.lesson_8_hibernate.dao.AbstractDao;
import lessons.lesson_8_hibernate.dao.finances.AccountDao;
import lessons.lesson_8_hibernate.dao.finances.CategoryDao;
import lessons.lesson_8_hibernate.entities.finances.Account;
import lessons.lesson_8_hibernate.entities.finances.Category;
import lessons.lesson_8_hibernate.entities.finances.Operation;
import lessons.lesson_8_hibernate.entities.finances.Transaction;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.TransactionAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.TransactionNotFoundException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionDao extends AbstractDao<Transaction, Long> {
    private static final Logger logger = LoggerFactory.getLogger(TransactionDao.class);


    private final DataSource dataSource;
    private final AccountDao accountDao;
    private final CategoryDao categoryDao;

    public TransactionDao(
            DataSource dataSource,
            AccountDao accountDao,
            CategoryDao categoryDao
    ) {
        super(dataSource);
        this.dataSource = dataSource;
        this.accountDao = accountDao;
        this.categoryDao = categoryDao;
    }

    @Override
    public Transaction findById(Long id) throws SQLException {
        return executeFindByIdQuery(id);
    }

    @Override
    public List<Transaction> findAll() throws SQLException {
        return executeFindAllQuery();
    }

    /**
     * Inserts transaction entity into database
     * @param transaction to be inserted
     * @return created transaction entity
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     * @throws OperationFailedException if transaction id was not generated
     * @throws TransactionAlreadyExistsException if transaction found in database
     */
    @Override
    public Transaction insert(Transaction transaction) throws SQLException, OperationFailedException, TransactionAlreadyExistsException {
        boolean alreadyExists = checkIfAlreadyExists(transaction);
        if (alreadyExists) {
            throw new TransactionAlreadyExistsException("Transaction already exists");
        }

        return executeInsertQuery(transaction);
    }

    /**
     * Inserts transaction entity into database
     * @param transaction to be inserted
     * @param connection to be used for query execution
     * @return created transaction entity
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     * @throws OperationFailedException if transaction id was not generated
     * @throws TransactionAlreadyExistsException if transaction found in database
     */
    public Transaction insert(Transaction transaction, Connection connection) throws SQLException, OperationFailedException, TransactionAlreadyExistsException {
        boolean alreadyExists = checkIfAlreadyExists(transaction);
        if (alreadyExists) {
            throw new TransactionAlreadyExistsException("Transaction already exists");
        }
        return executeInsertQuery(connection, transaction);
    }

    /**
     * Returns updated transaction
     * @return updated transaction if success
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     * @throws TransactionNotFoundException if transaction not found in the database by id
     * @throws OperationFailedException if transaction id was not generated
     */
    @Override
    public Transaction update(Transaction transaction) throws SQLException, TransactionNotFoundException, OperationFailedException {
        try {
            executeUpdateQuery(transaction.getId(), transaction);
        } catch (DataNotFoundException e) {
            throw  new TransactionNotFoundException("Transaction " + transaction.getSum() + " not found in the database");
        }

        return transaction;
    }

    @Override
    public void delete(Transaction transaction) throws SQLException, OperationFailedException {
        executeDeleteQuery(transaction);
    }

    public List<Transaction> findAllByUserId(Long userId) throws SQLException {
        logger.debug("user id = " + userId);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     getFindByUserIdQuery()
             )) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                Transaction transaction = getDomainFromQueryResult(rs);
                transactions.add(transaction);
            }

            return transactions;
        }
    }

    public List<Transaction> findAllByUserIdToday(Long userId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     getFindByUserIdTodayQuery()
             )) {
            ps.setLong(1, userId);
            ps.setTimestamp(2,
                    Timestamp.from(Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT)).toInstant()));
            ps.setTimestamp(3,
                    Timestamp.from(Timestamp.valueOf(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT)).toInstant()));
            ResultSet rs = ps.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                Transaction transaction = getDomainFromQueryResult(rs);
                transactions.add(transaction);
            }

            return transactions;
        }
    }

    @Override
    protected String getInsertQuery() {
        return "INSERT INTO transactions (transfer, type, account_id, category_id, ts) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected String getFindByIdQuery() {
        return "SELECT * FROM transactions WHERE id=?";
    }

    private String getFindByUserIdQuery() {
        return "SELECT transactions.* " +
                "FROM transactions " +
                "INNER JOIN accounts " +
                "ON transactions.account_id=accounts.id " +
                "WHERE accounts.user_id=?";
    }

    private String getFindByUserIdTodayQuery() {
        return "SELECT transactions.* FROM transactions INNER JOIN accounts ON " +
                "transactions.account_id=accounts.id WHERE accounts.user_id=? " +
                "AND transactions.ts BETWEEN ? AND ?";
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT * FROM transactions ORDER BY ts DESC";
    }

    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM transactions WHERE id=?";
    }

    @Override
    public void setupFindByIdQuery(PreparedStatement ps, Long id) throws SQLException {
        ps.setLong(1, id);
    }

    @Override
    protected String getFindDomainQuery() {
        return "SELECT * FROM transactions WHERE transfer=? AND type=? AND account_id=? AND category_id=? AND ts=?";
    }

    @Override
    public void setupInsertQuery(PreparedStatement ps, Transaction transaction) throws SQLException {
        ps.setBigDecimal(1, transaction.getSum());
        ps.setString(2, transaction.getOperation().name());
        ps.setLong(3, transaction.getAccount().getId());
        ps.setLong(4, transaction.getCategory().getId());
        Timestamp timestamp = new Timestamp(transaction.getTimestamp().toEpochMilli());
        ps.setTimestamp(5, timestamp);
        logger.debug("Timestamp to insert: " + timestamp);
    }

    @Override
    public void setupDeleteQuery(PreparedStatement ps, Transaction transaction) throws SQLException {
        ps.setLong(1, transaction.getId());
    }

    @Override
    public void setupFindDomainQuery(PreparedStatement ps, Transaction transaction) throws SQLException {
        ps.setBigDecimal(1, transaction.getSum());
        ps.setString(2, transaction.getOperation().name());
        ps.setLong(3, transaction.getAccount().getId());
        ps.setLong(4, transaction.getCategory().getId());
        ps.setTimestamp(5, new Timestamp(transaction.getTimestamp().toEpochMilli()));
        logger.debug("Timestamp to find: " + Timestamp.from(transaction.getTimestamp()));
    }

    @Override
    public Transaction getDomainFromQueryResult(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        logger.debug("Transaction found from db: " + rs.getString("transfer"));
        transaction.setId(rs.getLong("id"));
        transaction.setSum(rs.getBigDecimal("transfer"));

        String operationType = rs.getString("type");

        boolean operationTypeCorrect = false;
        for (Operation operation:
                Operation.values()) {
            if (operation.name().equalsIgnoreCase(operationType)) {
                operationTypeCorrect = true;
                transaction.setOperation(operation);
                break;
            }
        }
        if (!operationTypeCorrect) {
            throw new SQLException("Transaction type mismatch");
        }

        Long accountId = rs.getLong("account_id");
        Account account = accountDao.findById(accountId);
        transaction.setAccount(account);

        Long categoryId = rs.getLong("category_id");
        Category category = categoryDao.findById(categoryId);
        transaction.setCategory(category);

        Timestamp timestamp = rs.getTimestamp("ts");
        logger.debug("Timestamp of transaction from db: " + timestamp);
        logger.debug("Timestamp Instant of transaction from db: " + timestamp.toInstant());
//        transaction.setTimestamp(timestamp.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime());
        transaction.setTimestamp(Instant.ofEpochMilli(timestamp.getTime()));

        return transaction;
    }

    @Override
    public void updateDomain(ResultSet rs, Transaction transaction) throws SQLException {
        rs.updateBigDecimal("transfer", transaction.getSum());
        rs.updateString("type", transaction.getOperation().name());
        rs.updateLong("account_id", transaction.getAccount().getId());
        rs.updateLong("category_id", transaction.getCategory().getId());
        rs.updateRow();
    }
}
