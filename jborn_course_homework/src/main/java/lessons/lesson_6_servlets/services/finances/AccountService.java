package lessons.lesson_6_servlets.services.finances;

import lessons.lesson_6_servlets.dao.finances.AccountDao;
import lessons.lesson_6_servlets.entities.finances.Account;
import lessons.lesson_6_servlets.entities.finances.Operation;
import lessons.lesson_6_servlets.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_6_servlets.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_6_servlets.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_6_servlets.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_6_servlets.services.AbstractService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class AccountService implements AbstractService<Account, Long> {
    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public Account findById(Long id) throws SQLException {
        return accountDao.findById(id);
    }

    public Account findById(Long id, Connection connection) throws SQLException {
        return accountDao.findById(id, connection);
    }

    @Override
    public List<Account> findAll() throws SQLException {
        return accountDao.findAll();
    }

    public List<Account> findAllByUserId(Long userId) throws SQLException {
        return accountDao.findAllByUserId(userId);
    }

    @Override
    public Account insert(Account account) throws SQLException, OperationFailedException, AccountAlreadyExistsException {
        return accountDao.insert(account);
    }

    @Override
    public Account update(Account account) throws SQLException, AccountNotFoundException, OperationFailedException {
        return accountDao.update(account);
    }

    /**
     * Updates total for the given account id with the given sum based on operation type. If operation type is credit,
     * sum is subtracted and if debet then sum is added
     * @param accountId id of the account where sum to be updated
     * @param sum to be updated by
     * @param connection to be used for query execution
     * @param operation type for transaction
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     * @throws AccountNotFoundException if account not found in the database by id
     * @throws AccountNotMatchException if there is not enough money at the account
     */
    void updateSum(Long accountId, BigDecimal sum, Connection connection, Operation operation) throws SQLException, AccountNotFoundException, AccountNotMatchException {
        accountDao.updateSum(accountId, sum, connection, operation);
    }

    @Override
    public void delete(Account account) throws SQLException, OperationFailedException {
        accountDao.delete(account);
    }
}
