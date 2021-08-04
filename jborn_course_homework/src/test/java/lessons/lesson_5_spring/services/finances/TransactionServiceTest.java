package lessons.lesson_5_spring.services.finances;

import lessons.lesson_5_spring.dao.finances.TransactionDao;
import lessons.lesson_5_spring.entities.finances.*;
import lessons.lesson_5_spring.entities.users.UserEntity;
import lessons.lesson_5_spring.exceptions.already_exists_exception.TransactionAlreadyExistsException;
import lessons.lesson_5_spring.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_5_spring.exceptions.not_found_exception.TransactionNotFoundException;
import lessons.lesson_5_spring.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_5_spring.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_5_spring.services.users.UserService;
import lessons.lesson_5_spring.terminal_views.TerminalConfiguration;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionServiceTest {
    Logger logger = LoggerFactory.getLogger(TransactionServiceTest.class);

    TransactionService mockedSubj;
    TransactionDao mockTransactionDao;
    CategoryService mockCategoryService;
    AccountService mockAccountService;
    DataSource mockDataSource;
    Connection mockConnection;

    ApplicationContext context;
    TransactionService subj;
    AccountService accountService;
    UserService userService;
    CategoryService categoryService;

    @BeforeAll
    static void init() {
        System.setProperty("jdbcUrl", "jdbc:h2:mem:test_database?currentSchema=finances;TRACE_LEVEL_FILE=4;TRACE_LEVEL_SYSTEM_OUT=3");
        System.setProperty("jdbcUsername", "admin");
        System.setProperty("jdbcPassword", "admin");
    }

    @BeforeEach
    void setUp() {
        mockTransactionDao = mock(TransactionDao.class);
        mockCategoryService = mock(CategoryService.class);
        mockAccountService = mock(AccountService.class);
        mockDataSource = mock(DataSource.class);
        mockConnection = mock(Connection.class);
        mockedSubj = new TransactionService(mockTransactionDao, mockCategoryService, mockAccountService, mockDataSource);

        context = new AnnotationConfigApplicationContext(TerminalConfiguration.class);
        subj = context.getBean(TransactionService.class);
        userService = context.getBean(UserService.class);
        accountService = context.getBean(AccountService.class);
        categoryService = context.getBean(CategoryService.class);
    }

    @Test
    @Order(value = 1)
    void commitTransaction_mocked_ok() throws Exception {
        Account from = new Account(
                1L,
                "my salary card",
                new BigDecimal("45000.00"),
                new AccountType(2L, "salary card"),
                new UserEntity());

        Account to = new Account(
                2L,
                "my credit card",
                new BigDecimal("1000.00"),
                new AccountType(2L, "credit card"),
                new UserEntity());
        BigDecimal sum = new BigDecimal("20000.00");
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        doNothing().when(mockConnection).setAutoCommit(false);
        when(mockAccountService.findById(1L, mockConnection)).thenReturn(from);
        when(mockAccountService.findById(2L, mockConnection)).thenReturn(to);
        when(mockCategoryService.getByTitle("transfer")).thenReturn(new Category());
        doAnswer(
                (invocationOnMock) -> {
                    from.setSum(from.getSum().subtract(sum));
                    return null;
                })
                .when(mockAccountService).updateSum(to.getId(), sum, mockConnection, Operation.DEBET);
        doAnswer(
                (invocationOnMock) -> {
                    to.setSum(to.getSum().add(sum));
                    return null;
                })
                .when(mockAccountService).updateSum(from.getId(), sum, mockConnection, Operation.CREDIT);
        when(mockTransactionDao.insert(any(Transaction.class))).thenReturn(null);
        doNothing().when(mockConnection).commit();

        mockedSubj.commitTransaction(from.getId(), to.getId(), sum);
        Assertions.assertEquals(0, new BigDecimal("25000.00").compareTo(from.getSum()));
        Assertions.assertEquals(0, new BigDecimal("21000.00").compareTo(to.getSum()));
    }

    @Test
    @Order(value = 2)
    void commitTransaction_mocked_transactionAccountNotFound() throws Exception {
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        doNothing().when(mockConnection).setAutoCommit(false);
        when(mockAccountService.findById(any(Long.class), any(Connection.class))).thenReturn(null);

        assertThrows(AccountNotFoundException.class,
                () -> mockedSubj.commitTransaction(1L, 2L, new BigDecimal("20000.00")));
    }

    @Test
    @Order(value = 3)
    void findById_ok() throws SQLException {
        assertNotNull(subj.findById(1L));
    }

    @Test
    @Order(value = 4)
    void findAll_ok() throws SQLException {
        assertEquals(6, subj.findAll().size());
    }

    @Test
    @Order(value = 5)
    void insert_ok() throws SQLException, OperationFailedException, TransactionAlreadyExistsException {
        Account account = accountService.findById(1L);
        Category category = categoryService.findById(5L);
        BigDecimal sum = new BigDecimal("-1000.00");

        Transaction transaction = new Transaction(
                sum,
                Operation.CREDIT,
                account,
                category
        );
        logger.debug("Created transaction " + transaction);
        transaction = subj.insert(transaction);
        Transaction transactionFRomDb = subj.findById(transaction.getId());
        logger.debug("Inserted transaction " + transactionFRomDb);
        assertNotNull(transactionFRomDb);
    }

    @Test
    @Order(value = 6)
    void insert_transactionAlreadyExists() throws Exception {
        Account account = accountService.findById(2L);
        Category category = categoryService.findById(5L);

        BigDecimal sum = new BigDecimal("-1000.00");

        Transaction transaction = new Transaction(
                sum,
                Operation.CREDIT,
                account,
                category
        );

        Transaction insertedTransaction = subj.insert(transaction);
//        subj.insert(transaction);
//        subj.insert(transaction);
//        subj.insert(transaction);
//        subj.insert(transaction);

        logger.debug(insertedTransaction.toString());
//        logger.debug(String.valueOf(Timestamp.from(transaction.getTimestamp())));
        assertThrows(TransactionAlreadyExistsException.class, () -> subj.insert(transaction));
    }

    @Test
    @Order(value = 7)
    void update_ok() throws SQLException, OperationFailedException, TransactionNotFoundException {
        Transaction transaction = subj.findById(1L);

        assertEquals(0, new BigDecimal("-2000.00").compareTo(transaction.getSum()));
        assertEquals("present", transaction.getCategory().getTitle());
        assertEquals(2L, transaction.getAccount().getId());
        assertEquals(Operation.CREDIT, transaction.getOperation());
        assertEquals(Timestamp.valueOf(LocalDateTime.of(
                LocalDate.of(2021, 06, 22),
                LocalTime.of(19, 10, 25))).toInstant(),
                transaction.getTimestamp());

        Category category = categoryService.findById(5L);
        Account account = accountService.findById(1L);
        transaction.setSum(new BigDecimal("0.25"));
        transaction.setCategory(category);
        transaction.setAccount(account);
        transaction.setOperation(Operation.DEBET);
        transaction.setTimestamp(Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT)).toInstant());

        subj.update(transaction);

        Transaction transactionFromDb = subj.findById(1L);
        assertEquals(0, new BigDecimal("0.25").compareTo(transactionFromDb.getSum()));
        assertEquals("loan payment", transactionFromDb.getCategory().getTitle());
        assertEquals(1L, transactionFromDb.getAccount().getId());
        assertEquals(Operation.DEBET, transactionFromDb.getOperation());
        assertEquals(Timestamp.valueOf(LocalDateTime.of(
                LocalDate.of(2021, 6, 22),
                LocalTime.of(19, 10, 25))).toInstant(),
                transactionFromDb.getTimestamp());
    }


    @Order(value = 5555)
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
                new BigDecimal("45000.00"),
                new AccountType(2L, "salary card"),
                user);

        Account to = new Account(
                "my credit card",
                new BigDecimal("1000.00"),
                new AccountType(3L, "credit card"),
                user);
        from = accountService.insert(from);
        to = accountService.insert(to);

        subj.commitTransaction(from.getId(), to.getId(), new BigDecimal("20000.00"));

        BigDecimal fromSum = accountService.findById(from.getId()).getSum();
        BigDecimal toSum = accountService.findById(to.getId()).getSum();
        logger.debug("From: " + fromSum + " to: " + toSum);
        assertEquals(2, subj.findAllByUserIdToday(user.getId()).size());
        Assertions.assertEquals(0, new BigDecimal("25000.00").compareTo(fromSum));
        Assertions.assertEquals(0, new BigDecimal("21000.00").compareTo(toSum));
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

    @Test
    void delete_ok() throws SQLException, OperationFailedException {
        List<Transaction> transactions = subj.findAllByUserId(1L);
        assertFalse(transactions.isEmpty());
        int size = transactions.size();
        subj.delete(transactions.get(0));
        assertEquals(subj.findAllByUserId(1L).size(), size - 1);
    }

}