package lessons.lesson_4.services.finances;

import lessons.lesson_4.dao.finances.AccountDao;
import lessons.lesson_4.entities.finances.Account;

import java.sql.SQLException;
import java.util.List;

public class AccountServiceImpl implements AccountService {
    private AccountDao accountDao;

    public AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public Account findById(Long id) throws SQLException {
        return accountDao.findById(id);
    }

    @Override
    public List<Account> findAll() throws SQLException {
        return accountDao.findAll();
    }

    public List<Account> findAllByUserId(Long userId) throws SQLException {
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

    @Override
    public boolean delete(Account account) throws SQLException {
        return accountDao.delete(account);
    }
}
