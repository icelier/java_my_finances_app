package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.dao.users.UserDao;
import lessons.lesson_9_spring_boot.entities.finances.*;
import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class TransactionDaoTest {

    @Autowired private TransactionDao subj;
    @Autowired private AccountTypeDao accountTypeDao;
    @Autowired private AccountDao accountDao;
    @Autowired private UserDao userDao;
    @Autowired private CategoryDao categoryDao;

    private UserEntity user;
    private Account firstAccount;
    private Account secondAccount;
    private Transaction transaction1;

    @Before
    public void setUp() throws Exception {
        user = new UserEntity(
                "user name",
                "user fullName",
                "user password",
                "user@mail.ru",
                35
        );
        user = userDao.save(user);
        Category transferCategory = categoryDao.findById(2L).orElse(null);

        AccountType accountType = accountTypeDao.findById(2L).orElse(null);
        firstAccount = new Account(
                "first account",
                new BigDecimal("1000"),
                accountType,
                user
        );
        secondAccount = new Account(
                "second account",
                new BigDecimal("2000"),
                accountType,
                user
        );
        firstAccount = accountDao.save(firstAccount);
        secondAccount = accountDao.save(secondAccount);

        BigDecimal sum1 = new BigDecimal("100");
        BigDecimal sum2 = new BigDecimal("200");
        transaction1 = new Transaction(
              sum1.negate(),
              Operation.CREDIT,
              firstAccount,
                transferCategory
        );
        Transaction transaction2 = new Transaction(
                sum1,
                Operation.DEBET,
                secondAccount,
                transferCategory
        );
        subj.save(transaction1);
        subj.save(transaction2);
        subj.detach(transaction1);
        subj.detach(transaction2);
    }

    @Test
    public void findAllByUserId_ok() {
        assertEquals(2, subj.findAllByUserId(user.getId()).size());
    }

    @Test
    public void findAllByUserIdToday_ok() {
        String beginTime = Timestamp.valueOf(
                LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT)
        ).toInstant().toString();
        String endTime = Timestamp.valueOf(
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT)
        ).toInstant().toString();

        assertEquals(
                2,
                subj.findAllByUserIdToday(user.getId(), beginTime, endTime).size());
    }

    @Test
    public void deleteTransactionById_ok() {
        subj.deleteTransactionById(transaction1.getId());

        assertNull(subj.findById(transaction1.getId()).orElse(null));
    }

    @Test
    public void deleteAllTransactions_ok() {
        subj.deleteAllTransactions();

        assertEquals(0, subj.findAll().size());
    }
}