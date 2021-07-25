package lessons.lesson_4_add_layers_and_factories.services.finances;

import lessons.lesson_4_add_layers_and_factories.dao.finances.AccountDao;
import lessons.lesson_4_add_layers_and_factories.entities.finances.Account;
import lessons.lesson_4_add_layers_and_factories.entities.finances.Operation;
import lessons.lesson_4_add_layers_and_factories.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_4_add_layers_and_factories.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_4_add_layers_and_factories.services.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AccountService implements Service<Account, Long> {
    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public Account findById(Long id) throws Exception {
        return accountDao.findById(id);
    }

    public Account findById(Long id, Connection connection) throws Exception {
        return accountDao.findById(id, connection);
    }

    @Override
    public List<Account> findAll() throws Exception {
        return accountDao.findAll();
    }

    public List<Account> findAllByUserId(Long userId) throws Exception {
        return accountDao.findAllByUserId(userId);
    }

    @Override
    public Account insert(Account account) throws SQLException {
        return accountDao.insert(account);
    }

    @Override
    public Account update(Account account) throws SQLException {
        return accountDao.update(account);
    }

    boolean updateSum(Account account, BigDecimal sum, Connection connection, Operation operation) throws Exception {
        try {
            accountDao.updateSum(account, sum, connection, operation);
        } catch (AccountNotFoundException | AccountNotMatchException notFoundExc) {
            return false;
        }

        return true;
    }

    @Override
    public boolean delete(Account account) throws SQLException {
        return accountDao.delete(account);
    }
}
