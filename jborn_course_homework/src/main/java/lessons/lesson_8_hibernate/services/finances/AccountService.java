package lessons.lesson_8_hibernate.services.finances;

import lessons.lesson_8_hibernate.dao.finances.AccountDao;
import lessons.lesson_8_hibernate.entities.finances.Account;
import lessons.lesson_8_hibernate.entities.finances.Operation;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_8_hibernate.services.AbstractService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService extends AbstractService<Account, Long> {
    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public Account findById(Long id) throws OperationFailedException {
        return accountDao.findById(id);
    }

    @Override
    public List<Account> findAll() throws OperationFailedException {
        return accountDao.findAll();
    }

    @Override
    public Account insert(Account account) throws OperationFailedException, AccountAlreadyExistsException {
        return accountDao.insert(account);
    }

    @Override
    public Account update(Account account) throws AccountNotFoundException, OperationFailedException {
        return accountDao.update(account);
    }

    /**
     * Updates total for the given account id with the given sum based on operation type. If operation type is credit,
     * sum is subtracted and if debet then sum is added
     * @param account where sum to be updated
     * @param sum to be updated by
     * @param operation type for transaction
     * @throws AccountNotFoundException if account not found in the database by id
     * @throws AccountNotMatchException if there is not enough money at the account
     */
    void updateSum(Account account, BigDecimal sum, Operation operation) throws AccountNotFoundException, AccountNotMatchException {
        accountDao.updateSum(account, sum, operation);
    }

    @Override
    public void delete(Account account) throws DataNotFoundException, OperationFailedException {
        accountDao.delete(account);
    }

    @Override
    public int deleteAll() throws OperationFailedException {
        return accountDao.deleteAll();
    }
}
