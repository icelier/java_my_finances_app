package lessons.lesson_8_hibernate.services.finances;

import lessons.lesson_8_hibernate.dao.finances.TransactionDao;
import lessons.lesson_8_hibernate.entities.finances.Account;
import lessons.lesson_8_hibernate.entities.finances.Category;
import lessons.lesson_8_hibernate.entities.finances.Operation;
import lessons.lesson_8_hibernate.entities.finances.Transaction;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.TransactionAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.TransactionNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_8_hibernate.services.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Service
public class TransactionService extends AbstractService<Transaction, Long> {
    Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionDao transactionDao;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final EntityManager entityManager;

    public TransactionService(TransactionDao transactionDao, CategoryService categoryService,
                              AccountService accountService, EntityManager entityManager) {
        this.transactionDao = transactionDao;
        this.categoryService = categoryService;
        this.accountService = accountService;
        this.entityManager = entityManager;
    }

    @Override
    public Transaction findById(Long id) throws OperationFailedException {
        return transactionDao.findById(id);
    }

    @Override
    public List<Transaction> findAll() throws OperationFailedException {
        return transactionDao.findAll();
    }

    @Override
    public Transaction insert(Transaction transaction) throws OperationFailedException, TransactionAlreadyExistsException {
        return transactionDao.insert(transaction);
    }

    @Override
    public Transaction update(Transaction transaction) throws OperationFailedException, TransactionNotFoundException {
        return transactionDao.update(transaction);
    }

    @Override
    public void delete(Transaction transaction) throws OperationFailedException, DataNotFoundException {
        transactionDao.delete(transaction);
    }

    public int deleteAll() throws OperationFailedException {
        return transactionDao.deleteAll();
    }

    public List<Transaction> findAllByUserId(Long userId) throws OperationFailedException {
        return transactionDao.findAllByUserId(userId);
    }

    public List<Transaction> findAllByUserIdToday(Long userId) throws OperationFailedException {
        return transactionDao.findAllByUserIdToday(userId);
    }

    /**
     * Commits transfer from one account to another,
     * inserts two transactions for debet and credit operations into database.
     * @param fromAccountId account id to withdraw money from
     * @param toAccountId account id to transfer money to
     * @param sum to be transferred
     * @throws OperationFailedException if transaction failed
     */
    public void commitTransaction(Long fromAccountId, Long toAccountId, BigDecimal sum) throws OperationFailedException {
        if (sum.compareTo(BigDecimal.ZERO) < 0) {
            throw new OperationFailedException("Sum for transfer should be more than zero");
        }
        EntityTransaction transaction = null;
        boolean hasSuccess = false;

        try {
            while(!hasSuccess) {
                if (transaction != null) {
                    logger.debug("Money transfer transactions is active = " + transaction.isActive());
                }
                transaction = entityManager.getTransaction();
                transaction.begin();
                Account fromAccount = accountService.findById(fromAccountId);
                if (fromAccount == null) {
                    throw new AccountNotFoundException("Account for withdraw not found");
                }
                entityManager.lock(fromAccount, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                Account toAccount = accountService.findById(toAccountId);
                if (toAccount == null) {
                    throw new AccountNotFoundException("Account for transfer not found");
                }
                entityManager.lock(toAccount, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                withdrawMoney(fromAccount, sum);
                transferMoney(toAccount, sum);
                try {
                    transaction.commit();
                    hasSuccess = true;
                } catch (OptimisticLockException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        } finally {
            rollbackTransaction(transaction);
        }
    }

    /**
     * Creates transaction entity, updates total of the given account in the database and
     * then inserts created transaction entity into database
     * @param fromAccount account to be withdrawn money from for transaction
     * @param sum to be updated by
     * @throws AccountNotMatchException if there is not enough money at the account
     * @throws AccountNotFoundException if account not found in the database by id
     * @throws OperationFailedException if transaction insertion failed
     * @throws TransactionAlreadyExistsException if transaction found in database
     * @throws CategoryNotFoundException if transaction category not found by title
     */
    private void withdrawMoney(Account fromAccount, BigDecimal sum) throws AccountNotMatchException, AccountNotFoundException, OperationFailedException, TransactionAlreadyExistsException, CategoryNotFoundException {
        Transaction transactionFrom = createTransaction(fromAccount, Operation.CREDIT, sum.negate(), "transfer");
        logger.debug("account in withdraw method, total =  " + fromAccount.getTotal());

        accountService.updateSum(fromAccount, sum, Operation.CREDIT);
        transactionDao.insert(transactionFrom, true);
    }

    /**
     * Creates transaction entity, updates total of the given account in the database and
     * then inserts created transaction entity into database
     * @param toAccount account to be transferred money to for transaction
     * @param sum to be updated by
     * @throws AccountNotMatchException if there is not enough money at the account
     * @throws AccountNotFoundException if account not found in the database by id
     * @throws OperationFailedException if transaction insertion failed
     * @throws TransactionAlreadyExistsException if transaction found in database
     * @throws CategoryNotFoundException if transaction category not found by title
     */
    private void transferMoney(Account toAccount, BigDecimal sum) throws AccountNotMatchException, AccountNotFoundException, OperationFailedException, TransactionAlreadyExistsException, CategoryNotFoundException {
        Transaction transactionTo = createTransaction(toAccount, Operation.DEBET, sum, "transfer");

        accountService.updateSum(toAccount, sum, Operation.DEBET);
        transactionDao.insert(transactionTo, true);
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
                                          String category) throws CategoryNotFoundException, OperationFailedException {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setOperation(operation);
        transaction.setSum(sum);
        Category categoryType = categoryService.findByTitle(category);
        if (categoryType == null) {
            throw new CategoryNotFoundException("Transaction category not found");
        }
        transaction.setCategory(categoryType);

        return transaction;
    }

    private void rollbackTransaction(EntityTransaction transaction) throws OperationFailedException {
        if (transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (Exception ex) {
                throw new OperationFailedException(ex.getMessage());
            }
        }
    }

}
