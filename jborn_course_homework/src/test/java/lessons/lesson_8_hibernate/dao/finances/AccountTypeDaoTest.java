package lessons.lesson_8_hibernate.dao.finances;

import lessons.lesson_8_hibernate.dao.JpaConfiguration;
import lessons.lesson_8_hibernate.entities.finances.AccountType;
import lessons.lesson_8_hibernate.entities.finances.Category;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.AccountTypeAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

class AccountTypeDaoTest {
    Logger logger = LoggerFactory.getLogger(TransactionDaoTest.class);
    ApplicationContext context;
    AccountTypeDao subj;
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
        subj = context.getBean(AccountTypeDao.class);
        categoryDao = context.getBean(CategoryDao.class);
        entityManager = context.getBean(EntityManager.class);
    }

    @Test
    void insert() throws AccountTypeAlreadyExistsException, OperationFailedException {
        AccountType accountType = new AccountType();
        accountType.setTitle("new type");

        subj.insert(accountType);
        assertNotNull(accountType.getId());
    }
}