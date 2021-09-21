package lessons.lesson_10_spring_security.services.finances;

import lessons.lesson_10_spring_security.dao.finances.CategoryDao;
import lessons.lesson_10_spring_security.dao.finances.TransactionDao;
import lessons.lesson_10_spring_security.entities.finances.Account;
import lessons.lesson_10_spring_security.entities.finances.Category;
import lessons.lesson_10_spring_security.entities.finances.Operation;
import lessons.lesson_10_spring_security.entities.finances.Transaction;
import lessons.lesson_10_spring_security.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_10_spring_security.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_10_spring_security.exceptions.not_found_exception.TransactionNotFoundException;
import lessons.lesson_10_spring_security.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_10_spring_security.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_10_spring_security.services.AbstractService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService implements AbstractService<Transaction, Long> {
    Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionDao transactionDao;
    private final CategoryDao categoryDao;
    private final AccountService accountService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Transaction findById(Long id) {
        return transactionDao.findById(id).orElse(null);
    }

    @Override
    public List<Transaction> findAll() {
        return transactionDao.findAll();
    }

    public List<Transaction> findAllByUserId(Long userId) {
        return transactionDao.findAllByUserId(userId);
    }

    public List<Transaction> findAllByUserIdToday(Long userId) {
        String beginTime = Timestamp.valueOf(
                LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT)
        ).toInstant().toString();
        String endTime = Timestamp.valueOf(
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT)
        ).toInstant().toString();

        return transactionDao.findAllByUserIdToday(userId, beginTime, endTime);
    }

    public List<Transaction> findAllByAccountId(Long accountId) {
        return transactionDao.findAllByAccountId(accountId);
    }

    /**
     * Returns null as new transaction is intended to be inserted
     * through commitTransaction() method only.
     * @param transaction object to save in database
     * @return null
     */
    @Transactional
    @Override
    public Transaction insert(Transaction transaction) {
        // insert new transaction thru commitTransaction only

        return null;
    }

    /**
     * Only returns entity retrieved from database by id due to all fields being non-updatable,
     * ignoring object with data for update.
     * @param transaction containing update information
     * @return retrieved entity from database
     */
    @Transactional
    @Override
    public Transaction update(Long id, Transaction transaction) {
        detach(transaction);

        // no updatable fields in the class
        return findById(id);
    }

    /**
     * Deletes the given entity from database.
     * @param transaction entity to be deleted
     */
    @Transactional
    @Override
    public void delete(Transaction transaction) {
        transactionDao.delete(transaction);
    }

    /**
     * Retrieves an entity from database by given id and throws exception, if no transaction found.
     * Deletes entity from database.
     * @param id of transaction to delete
     * @throws TransactionNotFoundException if transaction not found by given id
     * @throws OperationFailedException if delete failed
     */
    @Transactional
    public void deleteById(Long id) {
        Transaction transactionFromDb = findById(id);
        if (transactionFromDb == null) {
            throw new TransactionNotFoundException("Transaction with id " + id +" not found");
        }
        detach(transactionFromDb);

        int affectedRows = transactionDao.deleteTransactionById(id);
        if (affectedRows <= 0) {
            throw new OperationFailedException("Failed to delete transaction data");
        }
    }

    /**
     * Deletes all entities from database.
     */
    @Transactional
    @Override
    public void deleteAll() {
        transactionDao.deleteAllTransactions();
    }

    /**
     * UpdateData ignored due to class having no updatable fields.
     * @param transactionToUpdate entity from database to update
     * @param updateData object containing update information
     * @return given transaction entity
     */
    @Override
    public Transaction updateDomainWithNewData(Transaction transactionToUpdate, Transaction updateData) {

        return transactionToUpdate;
    }

    /**
     * Commits transaction from one account to another on the given sum.
     * First checks sum for negative and zero values, if yes throws exception.
     * Then tries to commit transaction catching OptimisticLockException and retrying if caught until success.
     * @param fromAccountId account id to withdraw money from
     * @param toAccountId account id to transfer money to
     * @param sum to be transferred
     * @throws AccountNotFoundException if origin or destination account not found in the database by id
     * @throws AccountNotMatchException if origin and destination accounts are the same,
     * if there is not enough money at the account
     * @throws CategoryNotFoundException if transaction category not found in the database by title
     * @throws OperationFailedException if sum less than or equals to zero
     */
    @Transactional(rollbackFor = AccountNotMatchException.class)
    public void commitMoneyTransaction(Long fromAccountId, Long toAccountId, BigDecimal sum)
            throws AccountNotMatchException {

        if (fromAccountId.equals(toAccountId)) {
            throw new AccountNotMatchException("Origin and destination accounts match");
        }
        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperationFailedException("Sum for transfer should be more than zero");
        }

        boolean hasSuccess = false;

        while(!hasSuccess) {
            try {
                transferMoney(fromAccountId, toAccountId, sum);
                hasSuccess = true;
            } catch (OptimisticLockException ex) {
                ex.printStackTrace();
                accountService.detach(accountService.findById(fromAccountId));
                accountService.detach(accountService.findById(toAccountId));
            }
        }
    }

    /**
     * Makes transfer of given sum from one account to another.
     * Retrieves fromAccount from database by given id and throws exception, if no account found, same for toAccount.
     * If account found in database by id, sets optimistic lock for its entity.
     * Then updates accounts total and inserts two transactions for credit and debet operations.
     * @param fromAccountId account id to withdraw money from
     * @param toAccountId account id to transfer money to
     * @param sum to be transferred
     * @throws AccountNotFoundException if origin or destination account not found in the database by id
     * @throws AccountNotMatchException if there is not enough money at the account
     * @throws CategoryNotFoundException if transaction category not found in the database by title
     */
    @Transactional(propagation = Propagation.NESTED, rollbackFor = AccountNotMatchException.class)
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal sum)
            throws AccountNotMatchException {

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
    }

    /**
     * Creates transaction entity, updates total of the given account and
     * then saves created transaction entity to database
     * Requires database transaction to be already began in order to implement update.
     * @param fromAccount account to be withdrawn money from
     * @param sum to be updated by
     * @throws AccountNotMatchException if there is not enough money at the account
     * @throws CategoryNotFoundException if transaction category for transfer not found by title
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void withdrawMoney(Account fromAccount, BigDecimal sum) throws AccountNotMatchException {
        Transaction transactionFrom = createTransaction(fromAccount, Operation.CREDIT, sum.negate(), "transfer");

        accountService.updateSum(fromAccount, sum, Operation.CREDIT);
        transactionDao.save(transactionFrom);
    }

    /**
     * Creates transaction entity, updates total of the given account and
     * then saves created transaction entity to database
     * Requires database transaction to be already began in order to implement update.
     * @param toAccount account to be transferred money to
     * @param sum to be updated by
     * @throws AccountNotMatchException if there is not enough money at the account
     * @throws CategoryNotFoundException if transaction category for transfer not found by title
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void transferMoney(Account toAccount, BigDecimal sum)
            throws AccountNotMatchException {
        Transaction transactionTo = createTransaction(toAccount, Operation.DEBET, sum, "transfer");

        accountService.updateSum(toAccount, sum, Operation.DEBET);
        transactionDao.save(transactionTo);
    }

    /**
     * Creates transaction object from given data
     * @param account to be set for transaction
     * @param operation to be set for transaction
     * @param sum to be set for transaction
     * @param category title to get transaction category
     * @return created transaction object
     * @throws CategoryNotFoundException if transaction category not found by title
     */
    private Transaction createTransaction(Account account,
                                          Operation operation,
                                          BigDecimal sum,
                                          String category) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setOperation(operation);
        transaction.setSum(sum);
        Category categoryType = categoryDao.findByTitle(category).orElse(null);
        if (categoryType == null) {
            throw new CategoryNotFoundException("Transaction category not found");
        }
        transaction.setCategory(categoryType);

        return transaction;
    }

    /**
     * Used to detach from persistent context the entity retrieved from database
     * @param transaction persistent entity
     */
    public void detach(Transaction transaction) {
        transactionDao.detach(transaction);
    }

}
