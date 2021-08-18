package lessons.lesson_6_servlets.services.finances;

import lessons.lesson_6_servlets.dao.finances.TransactionDao;
import lessons.lesson_6_servlets.entities.finances.Account;
import lessons.lesson_6_servlets.entities.finances.Category;
import lessons.lesson_6_servlets.entities.finances.Operation;
import lessons.lesson_6_servlets.entities.finances.Transaction;
import lessons.lesson_6_servlets.exceptions.already_exists_exception.TransactionAlreadyExistsException;
import lessons.lesson_6_servlets.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_6_servlets.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_6_servlets.exceptions.not_found_exception.TransactionNotFoundException;
import lessons.lesson_6_servlets.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_6_servlets.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_6_servlets.services.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class TransactionService implements AbstractService<Transaction, Long> {
    Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private TransactionDao transactionDao;
    private CategoryService categoryService;
    private AccountService accountService;
    private DataSource dataSource;

    public TransactionService(TransactionDao transactionDao, CategoryService categoryService, AccountService accountService, DataSource dataSource) {
        this.transactionDao = transactionDao;
        this.categoryService = categoryService;
        this.accountService = accountService;
        this.dataSource = dataSource;
    }

    @Override
    public Transaction findById(Long id) throws SQLException {
        return transactionDao.findById(id);
    }

    @Override
    public List<Transaction> findAll() throws SQLException {
        return transactionDao.findAll();
    }

    /**
     * Inserts transaction entity into database with id generation
     * @param transaction to be inserted
     * @return created transaction entity with id generated
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     * @throws OperationFailedException if transaction id was not generated
     * @throws TransactionAlreadyExistsException if transaction found in database
     */
    @Override
    public Transaction insert(Transaction transaction) throws SQLException, OperationFailedException, TransactionAlreadyExistsException {
        return transactionDao.insert(transaction);
    }

    @Override
    public Transaction update(Transaction transaction) throws SQLException, OperationFailedException, TransactionNotFoundException {
        return transactionDao.update(transaction);
    }

    @Override
    public void delete(Transaction transaction) throws SQLException, OperationFailedException {
        transactionDao.delete(transaction);
    }

    public List<Transaction> findAllByUserId(Long userId) throws SQLException {
        return transactionDao.findAllByUserId(userId);
    }

    public List<Transaction> findAllByUserIdToday(Long userId) throws SQLException {
        return transactionDao.findAllByUserIdToday(userId);
    }

    /**
     *
     * @param fromAccountId account id to withdraw money from
     * @param toAccountId account id to transfer money to
     * @param sum to be transferred
     * @throws AccountNotFoundException if origin or destination account not found in the database by id
     * @throws SQLException if database access error occurred, if underlying query to database is incorrect
     * @throws AccountNotMatchException if there is not enough money at the account
     * @throws OperationFailedException if transaction insertion into database failed
     * @throws TransactionAlreadyExistsException if transaction already found in database
     * @throws CategoryNotFoundException if transaction category not found in the database by title
     */
    public void commitTransaction(Long fromAccountId, Long toAccountId, BigDecimal sum) throws AccountNotFoundException, SQLException, AccountNotMatchException, OperationFailedException, TransactionAlreadyExistsException, CategoryNotFoundException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            Account fromAccount = accountService.findById(fromAccountId, connection);
            if (fromAccount == null) {
                throw new AccountNotFoundException("Account for withdraw not found");
            }
            Account toAccount = accountService.findById(toAccountId, connection);
            if (toAccount == null) {
                throw new AccountNotFoundException("Account for transfer not found");
            }

            withdrawMoney(fromAccount, sum, connection);
            transferMoney(toAccount, sum, connection);
            connection.commit();
        } catch (SQLException e) {
            catchTransactionalException(connection, e);
        } finally {
            processTransactionalFinallyBlock(connection);
        }
    }

    /**
     * Creates transaction entity, updates total of the given account in the database and
     * then inserts created transaction entity into database
     * @param fromAccount account to be withdrawn money from for transaction
     * @param sum to be updated by
     * @param connection to be used for query execution
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     * @throws AccountNotMatchException if there is not enough money at the account
     * @throws AccountNotFoundException if account not found in the database by id
     * @throws OperationFailedException if transaction insertion failed
     * @throws TransactionAlreadyExistsException if transaction found in database
     * @throws CategoryNotFoundException if transaction category not found by title
     */
    private void withdrawMoney(Account fromAccount, BigDecimal sum, Connection connection) throws SQLException, AccountNotMatchException, AccountNotFoundException, OperationFailedException, TransactionAlreadyExistsException, CategoryNotFoundException {
        Transaction transactionFrom = createTransaction(fromAccount, Operation.CREDIT, sum.negate(), "transfer");
        logger.debug("account in withdraw method, sum =  " + fromAccount.getSum());

        accountService.updateSum(fromAccount.getId(), sum, connection, Operation.CREDIT);
        transactionDao.insert(transactionFrom, connection);
    }

    /**
     * Creates transaction entity, updates total of the given account in the database and
     * then inserts created transaction entity into database
     * @param toAccount account to be transferred money to for transaction
     * @param sum to be updated by
     * @param connection to be used for query execution
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     * @throws AccountNotMatchException if there is not enough money at the account
     * @throws AccountNotFoundException if account not found in the database by id
     * @throws OperationFailedException if transaction insertion failed
     * @throws TransactionAlreadyExistsException if transaction found in database
     * @throws CategoryNotFoundException if transaction category not found by title
     */
    private void transferMoney(Account toAccount, BigDecimal sum, Connection connection) throws SQLException, AccountNotMatchException, AccountNotFoundException, OperationFailedException, TransactionAlreadyExistsException, CategoryNotFoundException {
        Transaction transactionTo = createTransaction(toAccount, Operation.DEBET, sum, "transfer");

        accountService.updateSum(toAccount.getId(), sum, connection, Operation.DEBET);
        transactionDao.insert(transactionTo, connection);
    }

    /**
     * Creates transaction object from given data
     * @param account to be set for transaction
     * @param operation to be set for transaction
     * @param sum to be set for transaction
     * @param category title to get category
     * @return created transaction object if success
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     * @throws CategoryNotFoundException if transaction category not found by title
     */
    private Transaction createTransaction(Account account,
                                          Operation operation,
                                          BigDecimal sum,
                                          String category) throws SQLException, CategoryNotFoundException {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setOperation(operation);
        transaction.setSum(sum);
        Category categoryType = categoryService.getByTitle(category);
        if (categoryType == null) {
            throw new CategoryNotFoundException("Transaction category not found");
        }
        transaction.setCategory(categoryType);

        return transaction;
    }

    private void catchTransactionalException(Connection connection, Exception e) throws SQLException {
        if (connection != null) {
            try {
                connection.rollback();
                e.printStackTrace();
                throw new SQLException("Failed to commit transaction");
            } catch (SQLException throwables) {
                throw new SQLException("Failed to commit transaction");
            }
        }
    }

    private void processTransactionalFinallyBlock(Connection connection) throws SQLException {
        if (connection != null) {
            connection.setAutoCommit(true);
            try {
                connection.close();
            } catch (SQLException e) {
                throw new SQLException("Failed to close connection");
            }
        }
    }
}
