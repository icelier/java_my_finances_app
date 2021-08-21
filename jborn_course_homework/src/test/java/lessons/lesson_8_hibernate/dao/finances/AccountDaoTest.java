package lessons.lesson_8_hibernate.dao.finances;

import lessons.lesson_8_hibernate.dao.JpaConfiguration;
import lessons.lesson_8_hibernate.dao.users.UserDao;
import lessons.lesson_8_hibernate.entities.finances.Account;
import lessons.lesson_8_hibernate.entities.finances.AccountType;
import lessons.lesson_8_hibernate.entities.users.UserEntity;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountDaoTest {

    Logger logger = LoggerFactory.getLogger(AccountDaoTest.class);
    ApplicationContext context;
    AccountDao subj;

    EntityManager entityManager;
    UserDao userDao;
    AccountTypeDao accountTypeDao;

    @BeforeAll
    static void init() {
        System.setProperty("jdbcUrl", "jdbc:h2:mem:test_database?currentSchema=finances");
        System.setProperty("jdbcUsername", "admin");
        System.setProperty("jdbcPassword", "admin");
        System.setProperty("jdbcDriver", "org.h2.Driver");
        System.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    }

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(JpaConfiguration.class);
        entityManager = context.getBean(EntityManager.class);
        subj = context.getBean(AccountDao.class);
        userDao = context.getBean(UserDao.class);
        accountTypeDao = context.getBean(AccountTypeDao.class);
    }

    @Test
    @Order(1)
    void insert_ok() throws OperationFailedException, AccountAlreadyExistsException {
        Account newAccount = new Account();
        UserEntity user = userDao.findById(1L);
        AccountType accountType = accountTypeDao.findById(6L);
        String name = "New account for user with id=1L";
        newAccount.setName(name);
        newAccount.setUser(user);
        newAccount.setType(accountType);
        newAccount.setTotal(new BigDecimal("1000.00"));

        Account insertedAccount = subj.insert(newAccount);

        assertNotNull(insertedAccount.getId());
        logger.info("Inserted account = " + insertedAccount);
    }

    @Test
    @Order(2)
    void findById_ok() throws OperationFailedException, AccountAlreadyExistsException {
        Account newAccount = new Account();
        UserEntity user = userDao.findById(1L);
        AccountType accountType = accountTypeDao.findById(6L);
        String name = "New second account for user with id=1L";
        newAccount.setName(name);
        newAccount.setUser(user);
        newAccount.setType(accountType);
        newAccount.setTotal(new BigDecimal("2000.00"));

        subj.insert(newAccount);
        entityManager.detach(newAccount);

        Account accountFromDb = subj.findById(newAccount.getId());

        assertNotNull(accountFromDb);
    }

    @Test
    @Order(3)
    void delete_ok() throws OperationFailedException, AccountAlreadyExistsException, DataNotFoundException {
        Account newAccount = new Account();
        UserEntity user = userDao.findById(1L);
        AccountType accountType = accountTypeDao.findById(6L);
        String name = "New third account for user with id=1L";
        newAccount.setName(name);
        newAccount.setUser(user);
        newAccount.setType(accountType);
        newAccount.setTotal(new BigDecimal("3000.00"));

        subj.insert(newAccount);
        subj.delete(newAccount);

        assertNull(subj.findById(newAccount.getId()));
    }

    @Test
    @Order(4)
    void update_ok() throws OperationFailedException, AccountNotFoundException {
        Account account = subj.findById(1L);
        entityManager.detach(account);

        UserEntity newUser = userDao.findById(2L);
        AccountType newAccountType = accountTypeDao.findById(6L);
        String newName = "New account name";
        BigDecimal newSum = new BigDecimal("10.00");
        account.setUser(newUser);
        account.setType(newAccountType);
        account.setName(newName);
        account.setTotal(newSum);

        subj.update(account);
        entityManager.detach(account);

        Account updatedAccount = subj.findById(1L);

        assertEquals(newName, updatedAccount.getName());
        assertEquals(newSum, updatedAccount.getTotal());
        assertNotEquals(newAccountType, updatedAccount.getType());
        assertNotEquals(newUser, updatedAccount.getUser());
    }

    @Test
    @Order(5)
    void deleteAll_ok() throws OperationFailedException {
        List<Account> accounts = subj.findAll();
        int deletedRows = subj.deleteAll();

        assertEquals(accounts.size(), deletedRows);
    }

    @Test
    @Order(6)
    void findAll_ok() throws OperationFailedException, AccountAlreadyExistsException {
        UserEntity newUser = userDao.findById(1L);
        AccountType newAccountType = accountTypeDao.findById(5L);
        Account account1 = new Account(
                "new account 1",
                new BigDecimal("1500.00"),
                newAccountType,
                newUser
        );

        Account account2 = new Account(
                "new account 2",
                new BigDecimal("2500.00"),
                newAccountType,
                newUser
        );

        subj.deleteAll();
        subj.insert(account1);
        subj.insert(account2);
        List<Account> accounts = subj.findAll();

        assertEquals(2, accounts.size());
    }

}