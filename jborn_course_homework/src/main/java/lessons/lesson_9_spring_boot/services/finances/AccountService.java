package lessons.lesson_9_spring_boot.services.finances;

import lessons.lesson_9_spring_boot.dao.finances.AccountDao;
import lessons.lesson_9_spring_boot.entities.finances.Account;
import lessons.lesson_9_spring_boot.entities.finances.Operation;
import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_9_spring_boot.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService implements AbstractService<Account, Long> {
    @Autowired
    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

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
        return accountDao.findByName(name);
    }

    @Transactional
    @Override
    public Account insert(Account account) throws AccountAlreadyExistsException {
        Account accountFromDb = accountDao
                .findByUserIdAndName(account.getUser().getId(), account.getName());
        if (accountFromDb != null) {
            throw new AccountAlreadyExistsException("Account with such name for given user already exists");
        }

        account = accountDao.save(account);

        return account;
    }

    @Transactional
    @Override
    public Account update(Account account) {
        account = accountDao.save(account);

        return account;
    }

    /**
     * Updates total for the given account id with the given sum based on operation type. If operation type is credit,
     * sum is subtracted and if debet then sum is added
     * @param account where sum to be updated
     * @param sum to be updated by
     * @param operation type for transaction
     * @throws AccountNotMatchException if there is not enough money at the account
     */
    @Transactional
    void updateSum(Account account, BigDecimal sum, Operation operation) throws AccountNotMatchException {
        BigDecimal total = account.getTotal();
        if (operation == Operation.CREDIT && (total.compareTo(sum) < 0)) {
            throw new AccountNotMatchException("Transaction sum is beyond current account total");
        }
        if (operation == Operation.CREDIT) {
            account.setTotal(total.subtract(sum));
        } else if (operation == Operation.DEBET) {
            account.setTotal(total.add(sum));
        }
        accountDao.save(account);
    }

    @Transactional
    @Override
    public void delete(Account account) {
        accountDao.delete(account);
    }

    @Transactional
    @Override
    public void deleteAll()  {
        accountDao.deleteAll();
    }
}
