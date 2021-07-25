package lessons.lesson_4_add_layers_and_factories.services.finances;

import lessons.lesson_4_add_layers_and_factories.dao.finances.TransactionDao;
import lessons.lesson_4_add_layers_and_factories.entities.finances.Account;
import lessons.lesson_4_add_layers_and_factories.entities.finances.Category;
import lessons.lesson_4_add_layers_and_factories.entities.finances.Operation;
import lessons.lesson_4_add_layers_and_factories.entities.finances.Transaction;
import lessons.lesson_4_add_layers_and_factories.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_4_add_layers_and_factories.services.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TransactionService implements Service<Transaction, Long> {
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
    public Transaction findById(Long id) throws Exception {
        return transactionDao.findById(id);
    }

    @Override
    public List<Transaction> findAll() throws Exception {
        return transactionDao.findAll();
    }

    @Override
    public Transaction insert(Transaction transaction) throws SQLException {
        return transactionDao.insert(transaction);
    }

    @Override
    public Transaction update(Transaction transaction) throws SQLException {
        return transactionDao.update(transaction);
    }

    @Override
    public boolean delete(Transaction transaction) throws SQLException {
        return transactionDao.delete(transaction);
    }

    public List<Transaction> findAllByUserId(Long userId) throws Exception {
        return transactionDao.findAllByUserId(userId);
    }

    public List<Transaction> findAllByUserIdToday(Long userId) throws Exception {
        return transactionDao.findAllByUserIdToday(userId);
    }

    public void commitTransaction(Long fromAccountId, Long toAccountId, BigDecimal sum) throws Exception {
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
            if (connection != null) {
                try {
                    connection.rollback();
                    e.printStackTrace();
                    throw new SQLException("Failed to commit transaction");
                } catch (SQLException throwables) {
                    throw new Exception("Failed to commit transaction");
                }
            }
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new Exception("Failed to commit transaction");
                }
            }
        }
    }


    private void withdrawMoney(Account fromAccount, BigDecimal sum, Connection connection) throws Exception {
        Transaction transactionFrom = createTransaction(fromAccount, Operation.CREDIT, sum.negate(), "transfer");
//        fromAccount.setSum(fromAccount.getSum().subtract(sum));

        logger.debug("account in withdraw method, sum =  " + fromAccount.getSum());
        accountService.updateSum(fromAccount, sum, connection, Operation.CREDIT);
        transactionDao.insert(transactionFrom, connection);
    }
    private void transferMoney(Account toAccount, BigDecimal sum, Connection connection) throws Exception {
        Transaction transactionTo = createTransaction(toAccount, Operation.DEBET, sum, "transfer");
//        toAccount.setSum(toAccount.getSum().add(sum));

        accountService.updateSum(toAccount, sum, connection, Operation.DEBET);
        transactionDao.insert(transactionTo, connection);
    }

    private Transaction createTransaction(Account account,
                                          Operation operation,
                                          BigDecimal sum,
                                          String category) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setOperation(operation);
        transaction.setSum(sum);
        Category categoryType = categoryService.getByTitle(category);
        if (categoryType == null) {
            throw new SQLException("Failed to commit transfer, no transfer category found");
        }
        transaction.setCategory(categoryType);

        return transaction;
    }
}
