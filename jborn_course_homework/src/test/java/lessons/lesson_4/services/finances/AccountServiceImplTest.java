package lessons.lesson_4.services.finances;

import lessons.lesson_4.dao.finances.AccountDao;
import lessons.lesson_4.entities.finances.Account;
import lessons.lesson_4.entities.finances.AccountType;
import lessons.lesson_4.entities.users.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class AccountServiceImplTest {
    AccountServiceImpl subj;
    AccountDao accountDao;

    @BeforeEach
    void setUp() {
        accountDao = mock(AccountDao.class);
        subj = new AccountServiceImpl(accountDao);
    }

    @Test
    void findById() {
        AccountType type = new AccountType();
        UserEntity user = new UserEntity();
        Account account = new Account(25L, "my first account", BigDecimal.valueOf(12500.50), type, user);
    }

    @Test
    void findAll() {
    }

    @Test
    void findAllByUserId() {
    }

    @Test
    void insert() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}