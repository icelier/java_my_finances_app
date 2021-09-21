package lessons.lesson_10_spring_security.services.finances;

import lessons.lesson_10_spring_security.dao.finances.AccountDao;
import lessons.lesson_10_spring_security.entities.finances.Account;
import lessons.lesson_10_spring_security.entities.finances.Operation;
import lessons.lesson_10_spring_security.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_10_spring_security.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_10_spring_security.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_10_spring_security.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_10_spring_security.services.AbstractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountService implements AbstractService<Account, Long> {
    private final AccountDao accountDao;

    @Override
    public Account findById(Long id) {
        return accountDao.findById(id).orElse(null);
    }

    @Override
    public List<Account> findAll() {
        return accountDao.findAll();
    }

    public List<Account> findAllByUserId(Long userId) {
        return accountDao.findAllByUserId(userId);
    }

    public Account findByName(String name) {
        return accountDao.findByName(name).orElse(null);
    }

    /**
     * Returns inserted entity with generated id.
     * First checks if account name for such user id already exists.
     * If yes throws exception, otherwise proceeds with entity insertion.
     * @param account object to save in database
     * @return persistent entity inserted into database with generated id
     * @throws AccountAlreadyExistsException if account name with such user id already exists
     */
    @Transactional
    @Override
    public Account insert(Account account) throws AccountAlreadyExistsException {
        Account accountFromDb = findByUserIdAndName(account.getUser().getId(), account.getName());
        if (accountFromDb != null) {
            throw new AccountAlreadyExistsException("Account with name " + account.getName() + " for given user already exists");
        }

        account = accountDao.save(account);

        return account;
    }

    private Account findByUserIdAndName(Long userId, String name) {
        return accountDao.findByUserIdAndName(userId, name).orElse(null);
    }

    /**
     * Retrieves an entity from database by given id. Throws exception, if no account found .
     * Updates only updatable fields from the given object.
     * Returns updated entity from database after update committed
     * @param id of account to update
     * @param account object containing update information
     * @return updated entity
     * @throws AccountNotFoundException if account not found by given id
     */
    @Transactional
    @Override
    public Account update(Long id, Account account) {
        Account accountFromDb = findById(id);
        if (accountFromDb == null) {
            throw new AccountNotFoundException("Account for id " + id + " not found");
        }
        updateDomainWithNewData(accountFromDb, account);
        //for integration tests
        accountDao.flush();

        detach(accountFromDb);

        return findById(id);
    }

    /**
     * Retrieves an entity from database by given id. Throws exception, if no account found.
     * Updates account name into database.
     * Returns updated entity from database after update committed
     * @param id of account to update
     * @param newName for update
     * @return updated entity
     * @throws AccountNotFoundException if account not found by given id
     * @throws OperationFailedException if update failed
     */
    @Transactional
    public Account updateAccountNameById(Long id, String newName) {
        Account account = findById(id);
        if (account == null) {
            throw new AccountNotFoundException("Account for id " + id + " not found");
        }
        detach(account);

        int affectedRows = accountDao.updateAccountNameById(newName, id);
        if (affectedRows <= 0) {
            throw new OperationFailedException("Failed to update data");
        }

        return findById(id);
    }

    /**
     * Updates total for the given account id with the given sum based on operation type.
     * If operation type is credit sum is subtracted and if debet then sum is added
     * Requires database transaction to be already began in order to implement update.
     * @param account persistent entity where total to be updated
     * @param sum to be updated by (whether subtracted or added)
     * @param operation type for transaction
     * @throws AccountNotMatchException if there is not enough money at the account for credit operation
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void updateSum(Account account, BigDecimal sum, Operation operation)
            throws AccountNotMatchException {
        if (operation == Operation.CREDIT && (account.getTotal().compareTo(sum) < 0)) {
            throw new AccountNotMatchException("Transaction sum is beyond current account total");
        }
        if (operation == Operation.CREDIT) {
            account.setTotal(account.getTotal().subtract(sum));
        } else if (operation == Operation.DEBET) {
            account.setTotal(account.getTotal().add(sum));
        }
    }

    /**
     * Deletes the given entity from database.
     * @param account entity to be deleted
     */
    @Transactional
    @Override
    public void delete(Account account) {
        accountDao.delete(account);
    }

    /**
     * Retrieves an entity from database by given id and throws exception, if no account found.
     * Deletes entity from database.
     * @param id of account to delete
     * @throws AccountNotFoundException if account not found by given id
     * @throws OperationFailedException if delete failed
     */
    @Transactional
    public void deleteById(Long id) {
        Account accountFromDb = findById(id);
        if (accountFromDb == null) {
            throw new AccountNotFoundException("Account with id " + id +" not found");
        }
        detach(accountFromDb);

        int affectedRows = accountDao.deleteAccountById(id);
        if (affectedRows <= 0) {
            throw new OperationFailedException("Failed to delete account data");
        }
    }

    /**
     * Deletes all entities from database.
     */
    @Transactional
    @Override
    public void deleteAll()  {
        accountDao.deleteAllAccounts();
    }

    /**
     * Sets values from fields of updateData object into updatable fields only
     * of the given entity from database
     * @param accountToUpdate entity from database to update
     * @param updateData object containing update information
     * @return given entity updated with values from updateData object
     */
    @Override
    public Account updateDomainWithNewData(Account accountToUpdate, Account updateData) {
        accountToUpdate.setName(updateData.getName());
        accountToUpdate.setTotal(updateData.getTotal());

        return accountToUpdate;
    }

    /**
     * Used to detach from persistent context the entity retrieved from database
     * @param account persistent entity
     */
    public void detach(Account account) {
        accountDao.detach(account);
    }
}
