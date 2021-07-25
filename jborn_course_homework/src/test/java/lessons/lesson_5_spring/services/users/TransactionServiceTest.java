package lessons.lesson_5_spring.services.users;

import lessons.lesson_5_spring.entities.finances.*;
import lessons.lesson_5_spring.entities.users.UserEntity;
import lessons.lesson_5_spring.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_5_spring.services.finances.AccountService;
import lessons.lesson_5_spring.services.finances.TransactionService;
import lessons.lesson_5_spring.terminal_views.TerminalConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@RunWith(MockitoJUnitRunner.class)
class TransactionServiceTest {
    Logger logger = LoggerFactory.getLogger(TransactionServiceTest.class);

//    TransactionService mockedSubj;
//    TransactionDao mockTransactionDao;
//    CategoryService mockCategoryService;
//    AccountService mockAccountService;
//    DataSource mockDataSource;
//    Connection mockConnection;

    ApplicationContext context;
    TransactionService subj;
    AccountService accountService;
    UserService userService;

    @BeforeAll
    static void init() {
        System.setProperty("jdbcUrl", "jdbc:h2:mem:test_database?currentSchema=finances");
        System.setProperty("jdbcUsername", "admin");
        System.setProperty("jdbcPassword", "admin");
    }

    @BeforeEach
    void setUp() throws Exception {
//        mockTransactionDao = mock(TransactionDao.class);
//        mockCategoryService = mock(CategoryService.class);
//        mockAccountService = mock(AccountService.class);
//        mockDataSource = mock(DataSource.class);
//        mockConnection = mock(Connection.class);
//        mockedSubj = new TransactionService(mockTransactionDao, mockCategoryService, mockAccountService, mockDataSource);
          context = new AnnotationConfigApplicationContext(TerminalConfiguration.class);
          subj = context.getBean(TransactionService.class);
          userService = context.getBean(UserService.class);
          accountService = context.getBean(AccountService.class);
    }

//    @Test
//    void commitTransaction_mocked_ok() throws Exception {
//        Account from = new Account(
//                1L,
//                "my salary card",
//                BigDecimal.valueOf(45000),
//                new AccountType(2L, "salary card"),
//                new UserEntity());
//
//        Account to = new Account(
//                2L,
//                "my credit card",
//                BigDecimal.valueOf(1000),
//                new AccountType(2L, "credit card"),
//                new UserEntity());
//        when(mockDataSource.getConnection()).thenReturn(mockConnection);
//        doNothing().when(mockConnection).setAutoCommit(false);
//        when(mockAccountService.findById(1L, mockConnection)).thenReturn(from);
//        when(mockAccountService.findById(2L, mockConnection)).thenReturn(to);
//        when(mockCategoryService.getByTitle("transfer")).thenReturn(new Category());
//        when(mockAccountService.updateSum(from, mockConnection, Operation.CREDIT)).thenReturn(true);
//        when(mockTransactionDao.insert(any(Transaction.class))).thenReturn(null);
//        when(mockAccountService.updateSum(to, mockConnection, Operation.DEBET)).thenReturn(true);
//        doNothing().when(mockConnection).commit();
//
//        mockedSubj.commitTransaction(from.getId(), to.getId(), BigDecimal.valueOf(20_000));
//        Assertions.assertEquals(BigDecimal.valueOf(25000), from.getSum());
//        Assertions.assertEquals(BigDecimal.valueOf(21000), to.getSum());
//    }
//
//    @Test
//    void commitTransaction_mocked_transactionAccountNotFound() throws Exception {
//        when(mockDataSource.getConnection()).thenReturn(mockConnection);
//        doNothing().when(mockConnection).setAutoCommit(false);
//        when(mockAccountService.findById(any(Long.class), any(Connection.class))).thenReturn(null);
//
//        assertThrows(AccountNotFoundException.class,
//                () -> mockedSubj.commitTransaction(1L, 2L, BigDecimal.valueOf(20_000)));
//    }

    @Test
    void commitTransaction_ok() throws Exception {
        UserEntity user = userService.findByUserNameAndPassword("user1", "123");
        if (user == null) {
            user = new UserEntity(
                    "user1",
                "User User",
                "123",
                "user1@mail.ru",
                15
            );
            user = userService.insert(user);
        }
        Account from = new Account(
                "my salary card",
                BigDecimal.valueOf(45000),
                new AccountType(2L, "salary card"),
                user);

        Account to = new Account(
                "my credit card",
                BigDecimal.valueOf(1000),
                new AccountType(3L, "credit card"),
                user);
        from = accountService.insert(from);
        to = accountService.insert(to);

        subj.commitTransaction(from.getId(), to.getId(), new BigDecimal("20000"));

        BigDecimal fromSum = accountService.findById(from.getId()).getSum();
        BigDecimal toSum = accountService.findById(to.getId()).getSum();
        logger.debug("From: " + fromSum + " to: " + toSum);
        Assertions.assertEquals(0, new BigDecimal("25000").compareTo(fromSum));
        Assertions.assertEquals(0, new BigDecimal("21000").compareTo(toSum));
    }

    @Test
    void commitTransaction_notEnoughMoney() throws Exception {
        UserEntity user = userService.findById(2L);
        List<Account> userAccounts = accountService.findAllByUserId(user.getId());
        assertTrue(userAccounts.size() >= 2);
        BigDecimal sumOnFirstAccount = userAccounts.get(0).getSum();
        sumOnFirstAccount = sumOnFirstAccount.add(new BigDecimal("1"));

        BigDecimal finalSumOnFirstAccount = sumOnFirstAccount;
        assertThrows(AccountNotMatchException.class,
                () -> subj.commitTransaction(
                        userAccounts.get(0).getId(),
                        userAccounts.get(1).getId(),
                        finalSumOnFirstAccount
                ));
    }

}