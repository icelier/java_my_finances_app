package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.dao.users.UserDao;
import lessons.lesson_9_spring_boot.entities.finances.Account;
import lessons.lesson_9_spring_boot.entities.finances.AccountType;
import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class AccountDaoTest {

    @Autowired private AccountDao subj;
    @Autowired private UserDao userDao;
    @Autowired private AccountTypeDao accountTypeDao;

    private UserEntity user;
    private Account account;
    private AccountType accountType;

    @Before
    public void setUp() {
        user = new UserEntity(
                "user name",
                "user fullName",
                "user password",
                "user@mail.ru",
                35
        );
        user = userDao.save(user);

        accountType = accountTypeDao.findById(1L).orElse(null);

        account = new Account(
                "new account",
                new BigDecimal("1000"),
                accountType,
                user
        );
        account.setTransactions(Collections.emptyList());
        subj.save(account);
        subj.detach(account);
    }

    @Test
    public void findAllByUserId_ok() {
        assertEquals(1, subj.findAllByUserId(user.getId()).size());
    }

    @Test
    public void findByName_ok() {
        Account accountFromDb = subj.findByName("new account").orElse(null);
        assertNotNull(accountFromDb);
    }

    @Test
    public void findByName_passUnknownName_returnNull() {
        assertNull(subj.findByName("unknown name").orElse(null));
    }

    @Test
    public void findByUserIdAndName_ok() {
        Account accountFromDb = subj.findByUserIdAndName(user.getId(), "new account").orElse(null);

        assertNotNull(accountFromDb);
    }

    @Test
    public void findByUserIdAndName_passUnknownName_returnNull() {
        assertNull(subj.findByUserIdAndName(user.getId(), "unknown name").orElse(null));
    }

    @Test
    public void findByUserIdAndName_passUnknownUserId_returnNull() {
        assertNull(subj.findByUserIdAndName(user.getId() + 1, "new account").orElse(null));
    }

    @Test
    public void updateAccountById_ok() {
        subj.updateAccountNameById("new account name", account.getId());

        assertEquals(
                "new account name",
                subj.findById(account.getId()).orElse(null).getName());
    }

    @Test
    public void updateAccountTotalById_ok() {
        subj.updateAccountTotalById(BigDecimal.ZERO, account.getId());

        assertEquals(
                BigDecimal.ZERO.setScale(2),
                subj.findById(account.getId()).orElse(null).getTotal());
    }

    @Test
    public void deleteAccountById_ok() {
        subj.deleteAccountById(account.getId());

        assertNull(subj.findById(account.getId()).orElse(null));
    }

    @Test
    public void deleteAllAccounts_ok() {
        subj.deleteAllAccounts();

        assertEquals(0, subj.findAll().size());
    }
}