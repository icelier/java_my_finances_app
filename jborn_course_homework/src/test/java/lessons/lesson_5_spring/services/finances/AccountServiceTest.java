package lessons.lesson_5_spring.services.finances;

import lessons.lesson_5_spring.entities.finances.Account;
import lessons.lesson_5_spring.entities.finances.AccountType;
import lessons.lesson_5_spring.entities.users.UserEntity;
import lessons.lesson_5_spring.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_5_spring.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_5_spring.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_5_spring.terminal_views.TerminalConfiguration;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountServiceTest {
    Logger logger = LoggerFactory.getLogger(AccountServiceTest.class);
    AccountService subj;
    static ApplicationContext context;

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
    }

    @Test
    @Order(value = 1)
    void insert_ok() throws SQLException, OperationFailedException, AccountAlreadyExistsException {
        Account account = new Account(
                "my first account",
                new BigDecimal("10000"),
                new AccountType(2L, "salary card"),
                new UserEntity(1L,
                        "user1",
                        "User User",
                        "123",
                        "user1@mail.ru",
                        15)
        );
        account = subj.insert(account);
        assertNotNull(subj.findById(account.getId()));
    }

    @Test
    @Order(value = 2)
    void insert_accountAlreadyExists() throws SQLException, OperationFailedException, AccountAlreadyExistsException {
        Account account = new Account(
                "my first account",
                new BigDecimal("10000"),
                new AccountType(2L, "salary card"),
                new UserEntity(1L,
                        "user1",
                        "User User",
                        "123",
                        "user1@mail.ru",
                        15)
        );
        assertThrows(AccountAlreadyExistsException.class, () -> subj.insert(account));
    }

    @Ignore
    @Test
    @Order(value = 3)
    void findById_ok() throws SQLException, OperationFailedException, AccountAlreadyExistsException {
        Account account = new Account(
                "my first account",
                new BigDecimal("10000"),
                new AccountType(2L, "salary card"),
                new UserEntity(1L,
                        "user1",
                        "User User",
                        "123",
                        "user1@mail.ru",
                        15)
        );
        account = subj.insert(account);
        assertNotNull(subj.findById(account.getId()));
    }

    @Test
    @Order(value = 3)
    void findAll_ok() throws SQLException {
        assertFalse(subj.findAll().isEmpty());
    }

}