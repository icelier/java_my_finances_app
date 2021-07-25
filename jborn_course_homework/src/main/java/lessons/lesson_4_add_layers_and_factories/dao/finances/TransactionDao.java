package lessons.lesson_4_add_layers_and_factories.dao.finances;

import lessons.lesson_4_add_layers_and_factories.entities.finances.Account;
import lessons.lesson_4_add_layers_and_factories.entities.finances.Category;
import lessons.lesson_4_add_layers_and_factories.entities.finances.Operation;
import lessons.lesson_4_add_layers_and_factories.entities.finances.Transaction;
import lessons.lesson_4_add_layers_and_factories.dao.Dao;
import lessons.lesson_4_add_layers_and_factories.singleton_factories.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao implements Dao<Transaction, Long> {
    private static final Logger logger = LoggerFactory.getLogger(TransactionDao.class);

    private final DataSource dataSource;

    public TransactionDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Transaction findById(Long id) throws Exception {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM transactions WHERE id=?"
            )) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            Transaction transaction = null;
            if (rs.next()) {
                transaction = getTransactionFromResult(rs);
            }

            return transaction;
        }
    }

    @Override
    public List<Transaction> findAll() throws Exception {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM transactions"
             )) {

            ResultSet rs = ps.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                Transaction transaction = getTransactionFromResult(rs);
                transactions.add(transaction);
            }

            return transactions;
        }
    }

    /**
     * Returns inserted transaction or null if transaction already exists
     * @return inserted transaction if success
     */
    @Override
    public Transaction insert(Transaction transaction) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO transactions (transfer, type, account_id, category_id, ts) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
            ps.setBigDecimal(1, transaction.getSum());
//            ps.setObject(2, transaction.getOperation(), java.sql.Types.OTHER);
            ps.setString(2, transaction.getOperation().name());
            ps.setLong(3, transaction.getAccount().getId());
            ps.setLong(4, transaction.getCategory().getId());
            Timestamp timestamp = Timestamp.valueOf(transaction.getTimestamp());
            ps.setTimestamp(5, timestamp);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to insert new transaction");
            }
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                transaction.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("Failed to insert new transaction, no ID obtained");
            }

            return transaction;
        }
    }

    public Transaction insert(Transaction transaction, Connection connection) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO transactions (transfer, type, account_id, category_id, ts) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
            ps.setBigDecimal(1, transaction.getSum());
            ps.setString(2, transaction.getOperation().name());
//            ps.setObject(2, transaction.getOperation(), Types.OTHER);
            ps.setLong(3, transaction.getAccount().getId());
            ps.setLong(4, transaction.getCategory().getId());
            Timestamp timestamp = Timestamp.valueOf(transaction.getTimestamp());
            ps.setTimestamp(5, timestamp);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to insert new transaction");
            }
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                transaction.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("Failed to insert new transaction, no ID obtained");
            }

            return transaction;
        }
    }

    /**
     * Returns updated transaction or null if transaction does not exist
     * @return updated transaction if success
     */
    @Override
    public Transaction update(Transaction transaction) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM transactions WHERE id=?", 
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            )) {

            ps.setLong(1, transaction.getId());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return  null;
            } else {
                rs.updateBigDecimal("transfer", transaction.getSum());
                rs.updateString("type", transaction.getOperation().name());
                rs.updateLong("account_id", transaction.getAccount().getId());
                rs.updateLong("category_id", transaction.getCategory().getId());
                rs.updateRow();
            }

            return transaction;
        }
    }

    /**
     * Returns true if transaction deleted else false
     * @return true if transaction deleted
     */
    @Override
    public boolean delete(Transaction transaction) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM transactions WHERE id=?"
            )) {

            ps.setLong(1, transaction.getId());

            int deletedRowsCount = ps.executeUpdate();
            if (deletedRowsCount <= 0) {
                return false;
            }

            return true;
        }
    }

    private Transaction getTransactionFromResult(ResultSet result) throws Exception {
        Transaction transaction = new Transaction();
        logger.debug("Transaction found from db: " + result.getString("transfer"));
        transaction.setId(result.getLong("id"));
        transaction.setSum(result.getBigDecimal("transfer"));

        String operationType = result.getString("type");

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

        Long accountId = result.getLong("account_id");
        Account account = DaoFactory.getAccountDao().findById(accountId);
        transaction.setAccount(account);

        Long categoryId = result.getLong("category_id");
        Category category = DaoFactory.getCategoryDao().findById(categoryId);
        transaction.setCategory(category);

        Timestamp timestamp = result.getTimestamp("ts");
        transaction.setTimestamp(timestamp.toLocalDateTime());

        return transaction;
    }

    public List<Transaction> findAllByUserId(Long userId) throws Exception {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT transactions.* " +
                             "FROM transactions " +
                             "INNER JOIN accounts " +
                             "ON transactions.account_id=accounts.id " +
                             "WHERE accounts.user_id=?"
             )) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                Transaction transaction = getTransactionFromResult(rs);
                transactions.add(transaction);
            }

            return transactions;
        }
    }

    public List<Transaction> findAllByUserIdToday(Long userId) throws Exception {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT transactions.* FROM transactions INNER JOIN accounts ON " +
                             "transactions.account_id=accounts.id WHERE accounts.user_id=?" +
                             "AND transactions.ts BETWEEN ? AND ?"
             )) {
            ps.setLong(1, userId);
            ps.setTimestamp(2,
                    Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT)));
            ps.setTimestamp(3,
                    Timestamp.valueOf(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT)));
            ResultSet rs = ps.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                Transaction transaction = getTransactionFromResult(rs);
                transactions.add(transaction);
            }

            return transactions;
        }
    }

}
