package lessons.lesson_8_hibernate.services.finances;

import lessons.lesson_8_hibernate.entities.finances.Account;
import lessons.lesson_8_hibernate.entities.finances.Category;
import lessons.lesson_8_hibernate.entities.finances.Operation;
import lessons.lesson_8_hibernate.entities.finances.Transaction;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.TransactionAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.TransactionNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_8_hibernate.services.ServiceConfiguration;
import lessons.lesson_8_hibernate.services.users.UserService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionServiceTest {
    Logger logger = LoggerFactory.getLogger(TransactionServiceTest.class);
    ApplicationContext context;
    TransactionService subj;
    AccountService accountService;
    CategoryService categoryService;
    EntityManager entityManager;
    UserService userService;

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
        context = new AnnotationConfigApplicationContext(ServiceConfiguration.class);
        subj = context.getBean(TransactionService.class);
        accountService = context.getBean(AccountService.class);
        categoryService = context.getBean(CategoryService.class);
        entityManager = context.getBean(EntityManager.class);
        userService = context.getBean(UserService.class);
    }

    @Test
    @Order(1)
    void insert_ok() throws OperationFailedException, TransactionAlreadyExistsException, CategoryNotFoundException {
        Transaction newTransaction = new Transaction();
        Category category = categoryService.findByTitle("salary");
        Account account = accountService.findById(2L);
        newTransaction.setSum(new BigDecimal("100000.00"));
        newTransaction.setCategory(category);
        newTransaction.setAccount(account);
        newTransaction.setOperation(Operation.DEBET);

        Transaction insertedTransaction = subj.insert(newTransaction);

        assertNotNull(insertedTransaction.getId());
        logger.info("Inserted transaction = " + insertedTransaction);
    }

    @Test
    @Order(2)
    void findById_ok() throws OperationFailedException, CategoryNotFoundException, TransactionAlreadyExistsException {
        Transaction newTransaction = new Transaction();
        Category category = categoryService.findByTitle("sport");
        Account account = accountService.findById(2L);
        newTransaction.setSum(new BigDecimal("-10000.00"));
        newTransaction.setCategory(category);
        newTransaction.setAccount(account);
        newTransaction.setOperation(Operation.CREDIT);

        Transaction insertedTransaction = subj.insert(newTransaction);
        entityManager.detach(insertedTransaction);
        Transaction transactionFromDb = subj.findById(insertedTransaction.getId());

        assertNotNull(transactionFromDb);
    }

    @Test
    @Order(3)
    void delete_ok() throws DataNotFoundException, OperationFailedException, TransactionAlreadyExistsException {
        Transaction newTransaction = new Transaction();
        Category category = categoryService.findByTitle("gasoline");
        Account account = accountService.findById(1L);
        newTransaction.setSum(new BigDecimal("-2500.00"));
        newTransaction.setCategory(category);
        newTransaction.setAccount(account);
        newTransaction.setOperation(Operation.CREDIT);

        Transaction insertedTransaction = subj.insert(newTransaction);
        logger.debug("Inserted transaction = " + insertedTransaction);
        subj.delete(insertedTransaction);

        assertNull(subj.findById(insertedTransaction.getId()));
    }

    @Test
    @Order(4)
    void findAllByUserId_ok() throws OperationFailedException {
        List<Account> userOneAccounts = userService.findById(1L).getAccounts();
        logger.debug("Account transactions = " + userOneAccounts.get(0).getTransactions());
        long transactionCount = userOneAccounts.stream()
                .map(Account::getTransactions)
                .map(List::size)
                .reduce(0, Integer::sum);

        List<Transaction> userTransactions = subj.findAllByUserId(1L);

        assertEquals(transactionCount, userTransactions.size());
    }

    @Test
    @Order(5)
    void findAllByUserIdToday_ok() throws OperationFailedException {
        List<Transaction> userTransactions = subj.findAllByUserIdToday(1L);

        assertEquals(0, userTransactions.size());
    }

    @Test
    @Order(6)
    void commitTransaction_ok() throws OperationFailedException, AccountNotMatchException, TransactionAlreadyExistsException,
            CategoryNotFoundException, AccountNotFoundException {
        List<Account> userTwoAccounts = userService.findById(2L).getAccounts();
        Account accountFrom = userTwoAccounts.get(0);
        Account accountTo = userTwoAccounts.get(1);
        BigDecimal sum = accountFrom.getTotal();
        entityManager.detach(accountFrom);
        entityManager.detach(accountTo);

        subj.commitTransaction(accountFrom.getId(), accountTo.getId(), sum);

        entityManager.detach(accountFrom);
        entityManager.detach(accountTo);

        Account updatedAccountFrom = accountService.findById(accountFrom.getId());
        Account updatedAccountTo = accountService.findById(accountTo.getId());

        assertEquals(0, updatedAccountFrom.getTotal().compareTo(BigDecimal.ZERO));
        assertEquals(0, updatedAccountTo.getTotal().compareTo(sum.add(accountTo.getTotal())));

    }

    @Test
    @Order(6)
    void update_ok() throws OperationFailedException, CategoryNotFoundException, TransactionNotFoundException {
        Transaction transaction = subj.findById(1L);
        entityManager.detach(transaction);
        Category newCategory = categoryService.findByTitle("restaurants");
        Instant newTimestamp = Instant.now();
        Account newAccount = accountService.findById(3L);
        BigDecimal newSum = new BigDecimal("100.00");
        transaction.setCategory(newCategory);
        transaction.setTimestamp(newTimestamp);
        transaction.setAccount(newAccount);
        transaction.setSum(newSum);
        transaction.setOperation(Operation.DEBET);

        subj.update(transaction);
        entityManager.detach(transaction);

        Transaction updatedTransaction = subj.findById(1L);
        assertNotEquals("restaurants", updatedTransaction.getCategory().getTitle());
        assertNotEquals(newAccount, updatedTransaction.getAccount());
        logger.debug("New timestamp = " + newTimestamp + " and creation timestamp = " + updatedTransaction.getTimestamp());
        assertNotEquals(newTimestamp, updatedTransaction.getTimestamp());
        assertNotEquals(Operation.DEBET, updatedTransaction.getOperation());
        assertNotEquals(newSum, updatedTransaction.getSum());
    }

    @Test
    @Order(7)
    void deleteAll_ok() throws OperationFailedException {
        List<Transaction> transactions = subj.findAll();
        int deletedRows = subj.deleteAll();

        assertEquals(transactions.size(), deletedRows);
    }

    @Test
    @Order(8)
    void findAll_ok() throws OperationFailedException, CategoryNotFoundException, TransactionAlreadyExistsException {
        Category category = categoryService.findByTitle("utility bills");
        Account account = accountService.findById(1L);
        Transaction transaction1 = new Transaction(
                new BigDecimal("-500.00"),
                Operation.CREDIT,
                account,
                category
        );

        Transaction transaction2 = new Transaction(
                new BigDecimal("-1500.00"),
                Operation.CREDIT,
                account,
                category
        );

        subj.deleteAll();
        subj.insert(transaction1);
        subj.insert(transaction2);
        List<Transaction> transactions = subj.findAll();

        assertEquals(2, transactions.size());
    }

}