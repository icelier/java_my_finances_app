package lessons.lesson_8_hibernate.dao.finances;

import lessons.lesson_8_hibernate.dao.AbstractDao;
import lessons.lesson_8_hibernate.dao.users.UserDao;
import lessons.lesson_8_hibernate.entities.finances.Account;
import lessons.lesson_8_hibernate.entities.finances.Operation;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.DataAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Repository
public class AccountDao extends AbstractDao<Account, Long> {
    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    private final EntityManager entityManager;

    private final UserDao userDao;
    private final AccountTypeDao accountTypeDao;

    public AccountDao(EntityManager entityManager, UserDao userDao, AccountTypeDao accountTypeDao) {
        super(entityManager);
        this.entityManager = entityManager;
        this.userDao = userDao;
        this.accountTypeDao = accountTypeDao;
    }

    @Override
    public Account findById(Long id) throws OperationFailedException {
        Account account;
        try {
            account = entityManager.find(Account.class, id);
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return account;
    }

    @Override
    public List<Account> findAll() throws OperationFailedException {
        List<Account> accounts;
        try {
            TypedQuery<Account> query = entityManager.createQuery(getFindAllQuery(), Account.class);
            accounts = query.getResultList();
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return accounts;
    }

    @Override
    public Account insert(Account account) throws AccountAlreadyExistsException, OperationFailedException {
        logger.debug("AccountDao entityManager = " + entityManager);
        try {
            super.insert(account);
        } catch (DataAlreadyExistsException e) {
            throw new AccountAlreadyExistsException(e.getMessage());
        }

        return account;
    }

    @Override
    public Account update(Account account) throws AccountNotFoundException, OperationFailedException {
        boolean hasSuccess = false;
        EntityTransaction transaction = null;
        Account accountFromDb = null;

        while(!hasSuccess) {
            transaction = entityManager.getTransaction();
            transaction.begin();

            accountFromDb = findById(account.getId());
            if (accountFromDb == null) {
                throw new AccountNotFoundException("Account " + account.getName() + " not found in the database");
            }
            entityManager.lock(accountFromDb, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            accountFromDb = executeUpdateQuery(accountFromDb, account);
            try {
                transaction.commit();
                hasSuccess = true;
            } catch (OptimisticLockException e) {
                e.printStackTrace();
                rollbackTransaction(transaction);
            }
        }

        return accountFromDb;
    }

    @Override
    public void delete(Account account) throws OperationFailedException, DataNotFoundException {
        Account accountFromDb = findById(account.getId()) ;
        if (accountFromDb == null) {
            throw new AccountNotFoundException("Account " + account.getName() + " not found in the database");
        }
        super.delete(accountFromDb);
    }

    @Override
    public int deleteAll() throws OperationFailedException {
        List<Account> accounts = findAll();
        int deletedRows = 0;
        if (!accounts.isEmpty()) {
            deletedRows = super.deleteAll();
        }

        return deletedRows;
    }

//    public List<Account> findAllByUserId(Long userId) throws OperationFailedException {
//        try {
//            Query query = entityManager.createQuery(getFindByUserIdQuery());
//            query.setParameter("userId", userId);
//
//            List<Account> userAccounts = query.getResultList();
//
//            return userAccounts;
//        } catch (Exception e) {
//            throw new OperationFailedException(e.getMessage());
//        }
//    }

    public void updateSum(Account account, BigDecimal sum, Operation operation) throws AccountNotFoundException, AccountNotMatchException {
        BigDecimal total = account.getTotal();
        if (operation == Operation.CREDIT && (total.compareTo(sum) < 0)) {
            throw new AccountNotMatchException("Transaction sum is beyond current account total");
        }
        if (operation == Operation.CREDIT) {
            account.setTotal(total.subtract(sum));
        } else if (operation == Operation.DEBET) {
            account.setTotal(total.add(sum));
        }
    }

    @Override
    protected String getFindByIdQuery() {
        return "SELECT a FROM Account a WHERE a.id=:id";
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT a FROM Account a ORDER BY a.id ASC";
    }

    @Override
    protected String getDeleteAllQuery() {
        return "DELETE FROM Account a";
    }

//    private String getFindByUserIdQuery() {
//        return "SELECT a FROM Account a WHERE a.user_id=?";
//    }

    @Override
    protected void updateDomain(Account persistentDomain, Account account) {
        persistentDomain.setTotal(account.getTotal());
        persistentDomain.setName(account.getName());
    }

}
