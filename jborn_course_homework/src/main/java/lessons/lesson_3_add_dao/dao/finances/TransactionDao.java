package lessons.lesson_3_add_dao.dao.finances;

import lessons.lesson_3_add_dao.datasource.DataFactory;
import lessons.lesson_3_add_dao.entities.finances.Account;
import lessons.lesson_3_add_dao.entities.finances.Category;
import lessons.lesson_3_add_dao.entities.finances.Operation;
import lessons.lesson_3_add_dao.entities.finances.Transaction;
import lessons.lesson_3_add_dao.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao implements Dao<Transaction, Long> {
    private static final Logger logger = LoggerFactory.getLogger(TransactionDao.class);
    private static TransactionDao transactionDao;

    public static TransactionDao getTransactionDao() {
        if (transactionDao == null) {
            transactionDao = new TransactionDao();
        }
        return transactionDao;
    }

    private  TransactionDao() {}

    @Override
    public Transaction findById(Long id) throws SQLException {
        try(Connection connection = DataFactory.getConnection();
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
    public List<Transaction> findAll() throws SQLException {
        try (Connection connection = DataFactory.getConnection();
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
        try(Connection connection = DataFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO transactions (transfer, type, account_id, category_id, ts) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
            ps.setBigDecimal(1, transaction.getSum());
            ps.setObject(2, transaction.getOperation(), java.sql.Types.OTHER);
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
        try(Connection connection = DataFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM transactions WHERE id=?"
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
        try(Connection connection = DataFactory.getConnection();
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

    private Transaction getTransactionFromResult(ResultSet result) throws SQLException {
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
        Account account = AccountDao.getAccountDao().findById(accountId);
        transaction.setAccount(account);

        Long categoryId = result.getLong("category_id");
        Category category = CategoryDao.getCategoryDao().findById(categoryId);
        transaction.setCategory(category);

        Timestamp timestamp = result.getTimestamp("ts");
        transaction.setTimestamp(timestamp.toLocalDateTime());

        return transaction;
    }
}
