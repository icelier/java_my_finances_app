package lessons.lesson_9_spring_boot.services.finances;

import lessons.lesson_9_spring_boot.dao.finances.AccountDao;
import lessons.lesson_9_spring_boot.entities.finances.Account;
import lessons.lesson_9_spring_boot.entities.finances.AccountType;
import lessons.lesson_9_spring_boot.entities.finances.Operation;
import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.DataAlreadyExistsException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_9_spring_boot.exceptions.operation_failed.OperationFailedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@DataJpaTest
@RunWith(SpringRunner.class)
public class AccountServiceTest {
    @SpyBean private AccountService subj;
    @MockBean private AccountDao accountDao;
    private Account firstAccount;
    private Account secondAccount;
    private AccountType accountType;
    private UserEntity user;

    @Before
    public void setUp() {
        user = new UserEntity(
                100L,
                "user name",
                "user fullName",
                "user password",
                "user@mail.ru",
                35
        );

        accountType = new AccountType(2L, "salary card");

        firstAccount = new Account(
                1L,
                "first account",
                new BigDecimal("1000"),
                accountType,
                user
        );
        secondAccount = new Account(
                2L,
                "second account",
                new BigDecimal("2000"),
                accountType,
                user
        );
    }


    @Test
    public void findById_ok() {
        when(accountDao.findById(firstAccount.getId())).thenReturn(Optional.of(firstAccount));

        assertNotNull(subj.findById(firstAccount.getId()));
    }

    @Test
    public void findById_passUnknownId_returnNull() {
        when(accountDao.findById(100L)).thenReturn(Optional.empty());

        assertNull(subj.findById(100L));
    }

    @Test
    public void findAll_ok() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(firstAccount);
        accounts.add(secondAccount);

        when(accountDao.findAll()).thenReturn(accounts);

        assertEquals(2, subj.findAll().size());
    }

    @Test
    public void findAllByUserId_ok() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(firstAccount);
        accounts.add(secondAccount);
        when(accountDao.findAllByUserId(user.getId())).thenReturn(accounts);

        assertEquals(2, subj.findAllByUserId(user.getId()).size());
    }

    @Test
    public void findAllByUserId_passUnknownUserId_returnEmptyList() {
        when(accountDao.findAllByUserId(user.getId() + 1)).thenReturn(Collections.emptyList());

        assertEquals(0, subj.findAllByUserId(user.getId() + 1).size());
    }

    @Test
    public void findByName_ok() {
        when(accountDao.findByName(firstAccount.getName())).thenReturn(Optional.of(firstAccount));

        assertNotNull(subj.findByName("first account"));
    }

    @Test
    public void findByName_passUnknownName_returnNull() {
        when(accountDao.findByName("unknown name")).thenReturn(Optional.empty());

        assertNull(subj.findByName("unknown name"));
    }

    @Test
    public void insert_ok() throws AccountAlreadyExistsException {
        Account newAccount = new Account(
                "brand new account",
                new BigDecimal("2000"),
                accountType,
                user
        );

        when(accountDao.findByUserIdAndName(newAccount.getUser().getId(), newAccount.getName())).thenReturn(Optional.empty());
        Account finalAccount = newAccount;
        when(accountDao.save(newAccount)).then(
                (invocation) -> {
                    finalAccount.setId(3L);
                    return finalAccount;
                }
        );

        newAccount = subj.insert(newAccount);

        assertEquals(3L, (long) newAccount.getId());
    }

    @Test
    public void insertAccountWithSameNameAndSameUserId_throwsAccountAlreadyExistsException() {
        Account repeatedAccount = new Account(
                "first account",
                new BigDecimal("1000"),
                accountType,
                user
        );

        when(accountDao.findByUserIdAndName(repeatedAccount.getUser().getId(), repeatedAccount.getName()))
                .thenReturn(Optional.of(firstAccount));

        assertThrows(
                AccountAlreadyExistsException.class,
                () -> subj.insert(repeatedAccount)
        );
    }

    @Test
    public void update_ok_checkOnlyUpdatableFieldsUpdated() {
        UserEntity newUser = new UserEntity(
                101L,
                "second user name",
                "second user fullName",
                "second user password",
                "secondUser@mail.ru",
                35
        );
        Account updateData = new Account();
        updateData.setName("new name for accountFromDb");
        updateData.setTotal(BigDecimal.ZERO);
        updateData.setType(new AccountType(3L, "credit card"));
        updateData.setUser(newUser);

        when(accountDao.findById(firstAccount.getId())).thenReturn(Optional.of(firstAccount));

        firstAccount = subj.update(firstAccount.getId(), updateData);

        assertEquals("new name for accountFromDb", firstAccount.getName());
        assertEquals(BigDecimal.ZERO.setScale(2), firstAccount.getTotal());
        assertNotEquals(3L, (long) firstAccount.getType().getId());
        assertNotEquals(newUser.getId(), firstAccount.getUser().getId());
    }

    @Test
    public void updateSum_withCreditOperation_ok() throws AccountNotMatchException {
        subj.updateSum(firstAccount, new BigDecimal("500"), Operation.CREDIT);

        assertEquals(new BigDecimal("500.00"), firstAccount.getTotal());
    }

    @Test
    public void updateSum_withDebetOperation_ok() throws AccountNotMatchException {
        subj.updateSum(firstAccount, new BigDecimal("500"), Operation.DEBET);

        assertEquals(new BigDecimal("1500.00"), firstAccount.getTotal());
    }

    @Test
    public void updateSum_withCreditOperation_notEnoughMoney_throwsAccountNotMatchException() {
        assertThrows(
                AccountNotMatchException.class,
                () -> subj.updateSum(firstAccount, new BigDecimal("5000"), Operation.CREDIT)
        );
    }

    @Test
    public void delete_ok() {
        doNothing().when(accountDao).delete(firstAccount);

        when(accountDao.findById(1L)).thenReturn(Optional.empty());

        subj.delete(firstAccount);

        assertNull(accountDao.findById(1L).orElse(null));
    }

    @Test
    public void deleteAccountById_ok() {
        when(accountDao.findById(firstAccount.getId())).thenReturn(Optional.of(firstAccount));
        when(accountDao.deleteAccountById(firstAccount.getId())).thenReturn(1);

        subj.deleteById(firstAccount.getId());

        when(accountDao.findById(firstAccount.getId())).thenReturn(Optional.empty());

        assertNull(accountDao.findById(firstAccount.getId()).orElse(null));
    }

    @Test
    public void deleteAccountById_passUnknownId_throwsAccountNotFoundException() {
        when(accountDao.findById(firstAccount.getId() + 10)).thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> subj.deleteById(firstAccount.getId() + 10)
        );
    }

    @Test
    public void deleteAll_ok() {
        when(accountDao.deleteAllAccounts()).thenReturn(2);

        subj.deleteAll();

        when(accountDao.findAll()).thenReturn(Collections.emptyList());

        assertEquals(0, accountDao.findAll().size());
    }
}