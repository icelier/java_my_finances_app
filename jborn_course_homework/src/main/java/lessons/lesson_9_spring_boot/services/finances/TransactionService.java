package lessons.lesson_9_spring_boot.services.finances;

import lessons.lesson_9_spring_boot.dao.finances.TransactionDao;
import lessons.lesson_9_spring_boot.entities.finances.Account;
import lessons.lesson_9_spring_boot.entities.finances.Category;
import lessons.lesson_9_spring_boot.entities.finances.Operation;
import lessons.lesson_9_spring_boot.entities.finances.Transaction;
import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.TransactionAlreadyExistsException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_9_spring_boot.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_9_spring_boot.services.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Service
public class TransactionService implements AbstractService<Transaction, Long> {
    Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private final TransactionDao transactionDao;
    @Autowired
    private final CategoryService categoryService;
    @Autowired
    private final AccountService accountService;

    public TransactionService(TransactionDao transactionDao,
                              CategoryService categoryService,
                              AccountService accountService) {
        this.transactionDao = transactionDao;
        this.categoryService = categoryService;
        this.accountService = accountService;
    }

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

    public List<Transaction> findAllByUserIdToday(Long userId,
                                                  String beginTime,
                                                  String endTime) {
        return transactionDao.findAllByUserIdToday(userId, beginTime, endTime);
    }

    @Transactional
    @Override
    public Transaction insert(Transaction transaction) {
        transaction = transactionDao.save(transaction);

        return transaction;
    }

    @Transactional
    @Override
    public Transaction update(Transaction transaction) {
        transaction = transactionDao.save(transaction);

        return transaction;
    }

    @Transactional
    @Override
    public void delete(Transaction transaction) {
        transactionDao.delete(transaction);
    }

    @Transactional
    @Override
    public void deleteAll() {
        transactionDao.deleteAll();
    }

    /**
     *
     * @param fromAccountName account name to withdraw money from
     * @param toAccountName account name to transfer money to
     * @param sum to be transferred
     * @throws AccountNotFoundException if origin or destination account not found in the database by id
     * @throws AccountNotMatchException if there is not enough money at the account
     * @throws CategoryNotFoundException if transaction category not found in the database by title
     */
    @Transactional
    public void commitTransaction(String fromAccountName, String toAccountName, BigDecimal sum) throws AccountNotFoundException,
            AccountNotMatchException, CategoryNotFoundException {
        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Sum for transfer should be more than zero");
        }

        Account fromAccount = accountService.findByName(fromAccountName);
        if (fromAccount == null) {
            throw new AccountNotFoundException("Account for withdraw not found");
        }

        Account toAccount = accountService.findByName(toAccountName);
        if (toAccount == null) {
            throw new AccountNotFoundException("Account for transfer not found");
        }

        withdrawMoney(fromAccount, sum);
        transferMoney(toAccount, sum);

    }

    /**
     * Creates transaction entity, updates total of the given account in the database and
     * then inserts created transaction entity into database
     * @param fromAccount account to be withdrawn money from for transaction
     * @param sum to be updated by
     * @throws AccountNotMatchException if there is not enough money at the account
     * @throws CategoryNotFoundException if transaction category not found by title
     */
    @Transactional
    private void withdrawMoney(Account fromAccount, BigDecimal sum) throws AccountNotMatchException, CategoryNotFoundException {
        Transaction transactionFrom = createTransaction(fromAccount, Operation.CREDIT, sum.negate(), "transfer");
        logger.debug("account in withdraw method, total =  " + fromAccount.getTotal());

        accountService.updateSum(fromAccount, sum, Operation.CREDIT);
        transactionDao.save(transactionFrom);
    }

    /**
     * Creates transaction entity, updates total of the given account in the database and
     * then inserts created transaction entity into database
     * @param toAccount account to be transferred money to for transaction
     * @param sum to be updated by
     * @throws AccountNotMatchException if there is not enough money at the account
     * @throws CategoryNotFoundException if transaction category not found by title
     */
    @Transactional
    private void transferMoney(Account toAccount, BigDecimal sum) throws AccountNotMatchException, CategoryNotFoundException {
        Transaction transactionTo = createTransaction(toAccount, Operation.DEBET, sum, "transfer");

        accountService.updateSum(toAccount, sum, Operation.DEBET);
        transactionDao.save(transactionTo);
    }

    /**
     * Creates transaction object from given data
     * @param account to be set for transaction
     * @param operation to be set for transaction
     * @param sum to be set for transaction
     * @param category title to get category
     * @return created transaction object if success
     * @throws CategoryNotFoundException if transaction category not found by title
     */
    private Transaction createTransaction(Account account,
                                          Operation operation,
                                          BigDecimal sum,
                                          String category) throws CategoryNotFoundException {
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

}
