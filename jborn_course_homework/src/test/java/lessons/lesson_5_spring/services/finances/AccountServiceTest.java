package lessons.lesson_5_spring.services.finances;

import lessons.lesson_5_spring.dao.finances.AccountTypeDao;
import lessons.lesson_5_spring.entities.finances.Account;
import lessons.lesson_5_spring.entities.finances.AccountType;
import lessons.lesson_5_spring.entities.finances.Operation;
import lessons.lesson_5_spring.entities.users.UserEntity;
import lessons.lesson_5_spring.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_5_spring.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_5_spring.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_5_spring.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_5_spring.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_5_spring.services.users.UserService;
import lessons.lesson_5_spring.terminal_views.TerminalConfiguration;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountServiceTest {
    Logger logger = LoggerFactory.getLogger(AccountServiceTest.class);
    AccountService subj;
    UserService userService;
    AccountTypeDao accountTypeDao;
    DataSource dataSource;
    static ApplicationContext context;

    @Before
    @BeforeAll
    static void init() {
        System.setProperty("jdbcUrl", "jdbc:h2:mem:test_database?currentSchema=finances");
        System.setProperty("jdbcUsername", "admin");
        System.setProperty("jdbcPassword", "admin");
    }

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(TerminalConfiguration.class);
        subj = context.getBean(AccountService.class);
        userService = context.getBean(UserService.class);
        accountTypeDao = context.getBean(AccountTypeDao.class);
        dataSource = (DataSource) context.getBean("dataSource");
    }

    @Test
    @Order(value = 1)
    void insert_ok() throws SQLException, OperationFailedException, AccountAlreadyExistsException {
        UserEntity user = userService.findById(1L);

        Account account = new Account(
                "my first account",
                new BigDecimal("10000"),
                new AccountType(2L, "salary card"),
                user
        );
        account = subj.insert(account);
        assertNotNull(subj.findById(account.getId()));
    }

    @Test
    @Order(value = 2)
    void insert_accountAlreadyExists() throws SQLException {
        UserEntity user = userService.findById(1L);
        Account account = new Account(
                "my first account",
                new BigDecimal("10000"),
                new AccountType(2L, "salary card"),
                user
        );
        assertThrows(AccountAlreadyExistsException.class, () -> subj.insert(account));
    }

    @Test
    @Order(value = 3)
    void findById_ok() throws SQLException {
        assertNotNull(subj.findById(4L));
    }

    @Test
    @Order(value = 3)
    void findById_accountNotFound() throws SQLException {
        assertNull(subj.findById(100L));
    }

    @Test
    @Order(value = 4)
    void findAll_ok() throws SQLException {
        assertFalse(subj.findAll().isEmpty());
    }

    @Test
    @Order(value = 5)
    void findAllByUserId_ok() throws SQLException {
        assertEquals(2, subj.findAllByUserId(2L).size());
    }

    @Test
    @Order(value = 6)
    void update_ok() throws SQLException, OperationFailedException, UserAlreadyExistsException, AccountNotFoundException {
        Account account = subj.findById(2L);
        account.setName("changed name for account with id 2");
        account.setSum(new BigDecimal("0.25"));
        account.setType(new AccountType(1L, "cash"));

        UserEntity user =  new UserEntity(
                "user1",
                "User User",
                "123",
                "user1@mail.ru",
                15
        );
        user = userService.insert(user);
        account.setUser(user);
        subj.update(account);

        assertEquals("changed name for account with id 2", subj.findById(2L).getName());
        assertEquals(0, subj.findById(2L).getSum().compareTo(new BigDecimal("0.25")));
        assertEquals(accountTypeDao.findById(1L), subj.findById(2L).getType());
    }

    @Test
    @Order(value = 7)
    void updateSum_ok() throws SQLException, AccountNotMatchException, AccountNotFoundException {
        Connection connection = dataSource.getConnection();
        BigDecimal sum = new BigDecimal("1000000");
        Long accountId = 2L;

        subj.updateSum(accountId, sum, connection, Operation.DEBET);
        Account accountFromDb = subj.findById(2L);
        assertEquals(0, accountFromDb.getSum().compareTo(new BigDecimal("1000000.25")));

        BigDecimal sum2 = new BigDecimal("1000000.25");
        subj.updateSum(accountId, sum2, connection, Operation.CREDIT);
        Account accountFromDb2 = subj.findById(2L);
        assertEquals(0, accountFromDb2.getSum().compareTo(BigDecimal.ZERO));
    }

    @Test
    @Order(value = 8)
    void updateSum_notEnoughMoney() throws SQLException {
        Connection connection = dataSource.getConnection();
        BigDecimal sum = new BigDecimal("1000000");
        Long accountId = 2L;

        assertThrows(AccountNotMatchException.class, () -> subj.updateSum(accountId, sum, connection, Operation.CREDIT));
    }

    @Test
    @Order(value = 9)
    void delete_ok() throws SQLException, OperationFailedException {
        subj.delete(subj.findById(3L));
        assertNull(subj.findById(3L));
    }

    @Test
    @Order(value = 10)
    void delete_okOnCascadeWithUserDeletion() throws SQLException, OperationFailedException {
        UserEntity user = userService.findByUserName("user1");
        userService.delete(user);
        assertNull(subj.findById(2L));
    }

}