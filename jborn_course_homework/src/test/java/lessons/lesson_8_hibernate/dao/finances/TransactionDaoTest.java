package lessons.lesson_8_hibernate.dao.finances;

import lessons.lesson_8_hibernate.dao.JpaConfiguration;

import lessons.lesson_8_hibernate.entities.finances.*;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.TransactionAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.TransactionNotFoundException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionDaoTest {
    Logger logger = LoggerFactory.getLogger(TransactionDaoTest.class);
    ApplicationContext context;
    TransactionDao subj;
    AccountDao accountDao;
    CategoryDao categoryDao;
    EntityManager entityManager;

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
        subj = context.getBean(TransactionDao.class);
        accountDao = context.getBean(AccountDao.class);
        categoryDao = context.getBean(CategoryDao.class);
        entityManager = context.getBean(EntityManager.class);
    }

    @Test
    @Order(1)
    void insert_ok() throws OperationFailedException, TransactionAlreadyExistsException, CategoryNotFoundException {
        Transaction newTransaction = new Transaction();
        Category category = categoryDao.findByTitle("utility bills");
//        Account account = new Account("brand new account", new BigDecimal("10000.00"),
//                accountTypeDao.findById(6L), userDao.findById(1L));
//        accountDao.insert(account);
//        assertNotNull(account.getId());
        Account account = accountDao.findById(1L);
        newTransaction.setSum(new BigDecimal("-1000.00"));
        newTransaction.setCategory(category);
        newTransaction.setAccount(account);
        newTransaction.setOperation(Operation.CREDIT);

        Transaction insertedTransaction = subj.insert(newTransaction);

        assertNotNull(insertedTransaction.getId());
        logger.info("Inserted transaction = " + insertedTransaction);
    }

    @Test
    @Order(2)
    void findById_ok() throws OperationFailedException {
        Transaction transactionFromDb = subj.findById(1L);
        assertNotNull(transactionFromDb);
    }

    @Test
    @Order(3)
    void delete_ok() throws DataNotFoundException, OperationFailedException, TransactionAlreadyExistsException {
        Transaction newTransaction = new Transaction();
        Category category = categoryDao.findByTitle("utility bills");
        Account account = accountDao.findById(1L);
        newTransaction.setSum(new BigDecimal("-2000.00"));
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
    void update_ok() throws OperationFailedException, CategoryNotFoundException, TransactionNotFoundException {
        Transaction transaction = subj.findById(1L);
        entityManager.detach(transaction);

        Category newCategory = categoryDao.findByTitle("restaurants");
        Instant newTimestamp = Instant.now();
        Account newAccount = accountDao.findById(3L);
        BigDecimal newSum = new BigDecimal("100.00");
        transaction.setCategory(newCategory);
        transaction.setTimestamp(newTimestamp);
        transaction.setAccount(newAccount);
        transaction.setOperation(Operation.DEBET);
        transaction.setSum(newSum);

        transaction = subj.update(transaction);
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
    @Order(4)
    void update_checkIfTransactionRollbackedWhenOptimisticLockExceptionCaught() throws OperationFailedException, TransactionNotFoundException {
        EntityTransaction transaction = startRolbackedTransaction();
        logger.debug("Transaction is active: " + transaction.isActive());
    }

    private EntityTransaction startRolbackedTransaction() throws TransactionNotFoundException, OperationFailedException {
        boolean hasSuccess = false;
        EntityTransaction entityTransaction = null;
        Transaction transactionFromDb = null;

        int counter = 0;
        while(!hasSuccess) {
            if (counter > 2) {break;}
            if (entityTransaction != null) {
                logger.debug("Money transfer transactions is active = " + entityTransaction.isActive());
            }
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            transactionFromDb = subj.findById(1L);
            if (transactionFromDb == null) {
                throw new TransactionNotFoundException("Transaction not found in the database");
            }
            entityManager.lock(transactionFromDb, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            transactionFromDb.setSum(new BigDecimal("-10.00"));
            try {
                counter++;
                throw new OptimisticLockException("My OLE");
//                entityTransaction.commit();
//                hasSuccess = true;
            } catch (OptimisticLockException e) {
                e.printStackTrace();
                if (entityTransaction.isActive()) {
                    try {
                        entityTransaction.rollback();
                    } catch (Exception ex) {
                        throw new OperationFailedException(ex.getMessage());
                    }
                }
            }
        }

        return entityTransaction;
    }

    @Test
    @Order(5)
    void deleteAll_ok() throws OperationFailedException {
        List<Transaction> transactions = subj.findAll();
        int deletedRows = subj.deleteAll();


        assertEquals(transactions.size(), deletedRows);
    }

    @Test
    @Order(6)
    void findAll_ok() throws OperationFailedException, CategoryNotFoundException, TransactionAlreadyExistsException {
        Category category = categoryDao.findByTitle("utility bills");
        Account account = accountDao.findById(1L);
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