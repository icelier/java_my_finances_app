package lessons.lesson_9_spring_boot.services.finances;

import lessons.lesson_9_spring_boot.dao.finances.CategoryDao;
import lessons.lesson_9_spring_boot.dao.finances.TransactionDao;
import lessons.lesson_9_spring_boot.entities.finances.*;
import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.TransactionNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_9_spring_boot.exceptions.operation_failed.OperationFailedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class TransactionServiceTest {
    @SpyBean private TransactionService subj;

    @Autowired
    private TestEntityManager entityManager;

    @MockBean private AccountService accountService;
    @MockBean private TransactionDao transactionDao;
    @MockBean private CategoryDao categoryDao;


    private Transaction transaction;
    private UserEntity user;
    private AccountType accountType;
    private Account account;
    private Category transferCategory;

    @Before
    public void setUp() {
        user = new UserEntity(
                1L,
                "user name",
                "user fullName",
                "user password",
                "user@mail.ru",
                35
        );

        accountType = new AccountType(3L, "credit card");

        account = new Account(
                1L,
                "new account",
                new BigDecimal("10000"),
                accountType,
                user
        );

        transferCategory = new Category(2L, "transfer");
        transaction = new Transaction(
                1L,
                new BigDecimal("-1000"),
                Operation.CREDIT,
                account,
                transferCategory
        );
    }

    @Test
    public void findById_ok() {
        when(transactionDao.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        assertNotNull(subj.findById(transaction.getId()));
    }

    @Test
    public void findAll_ok() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        when(transactionDao.findAll()).thenReturn(transactions);

        assertEquals(1, subj.findAll().size());
    }

    @Test
    public void findAllByUserId_ok() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        when(transactionDao.findAllByUserId(user.getId())).thenReturn(transactions);

        assertEquals(1, subj.findAllByUserId(user.getId()).size());
    }

    @Test
    public void findAllByUserIdToday_ok() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        when(transactionDao.findAllByUserIdToday(eq(user.getId()), anyString(), anyString())).thenReturn(transactions);

        assertEquals(
                1, subj.findAllByUserIdToday(user.getId()).size());
    }

    @Test
    public void insert_ok() {
        Transaction transaction2 = new Transaction(
                new BigDecimal("20000"),
                Operation.DEBET,
                account,
                new Category(1L, "salary")
        );

        assertNull(subj.insert(transaction2));
    }

    @Test
    public void update_ok() {
        Account account2 = new Account(
                2L,
                "name",
                BigDecimal.ZERO,
                accountType,
                user
        );
        Category category = new Category(2L, "restaurants");
        Instant timestamp = Instant.now();
        Transaction updateData = new Transaction(
                BigDecimal.ZERO,
                Operation.DEBET,
                account2,
                category
        );
        updateData.setTimestamp(timestamp);

        when(transactionDao.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        transaction = subj.update(transaction.getId(), updateData);

        assertNotEquals(account2, transaction.getAccount());
        assertNotEquals(BigDecimal.ZERO, transaction.getSum());
        assertNotEquals(category, transaction.getCategory());
        assertNotEquals(timestamp, transaction.getTimestamp());
        assertNotEquals(Operation.DEBET, transaction.getOperation());
    }

    @Test
    public void delete_ok() {
        doNothing().when(transactionDao).delete(transaction);

        when(transactionDao.findById(1L)).thenReturn(Optional.empty());

        subj.delete(transaction);

        assertNull(transactionDao.findById(transaction.getId()).orElse(null));
    }

    @Test
    public void deleteTransactionById_ok() {
        when(transactionDao.findById(transaction.getId())).thenReturn(Optional.of(transaction));
        when(transactionDao.deleteTransactionById(transaction.getId())).thenReturn(1);

        subj.deleteById(transaction.getId());

        when(transactionDao.findById(transaction.getId())).thenReturn(Optional.empty());

        assertNull(transactionDao.findById(transaction.getId()).orElse(null));
    }

    @Test
    public void deleteTransactionById_passUnknownId_throwsTransactionNotFoundException() {
        when(transactionDao.findById(transaction.getId() + 1)).thenReturn(Optional.empty());

        assertThrows(
                TransactionNotFoundException.class,
                () -> subj.deleteById(transaction.getId() + 1)
        );
    }

    @Test
    public void deleteAll_ok() {
        when(transactionDao.deleteAllTransactions()).thenReturn(1);

        subj.deleteAll();

        when(transactionDao.findAll()).thenReturn(Collections.emptyList());

        assertEquals(0, transactionDao.findAll().size());
    }

    @Test
    public void commitTransaction_ok() throws AccountNotMatchException {
        Account account2 = new Account(
                "destination account",
                BigDecimal.ZERO,
                accountType,
                user
        );
        entityManager.persist(account);
        entityManager.persist(account2);

        Long fromId = account.getId();
        Long toId = account2.getId();
        BigDecimal sum = new BigDecimal("5000.00");

        when(accountService.findById(fromId)).thenReturn(account);
        when(accountService.findById(toId)).thenReturn(account2);
        doAnswer((invocation) -> {
            account.setTotal(account.getTotal().subtract(sum));
            return null;
        })
                .when(accountService).updateSum(account, sum, Operation.CREDIT);
        Account finalAccount = account2;
        doAnswer((invocation) -> {
            finalAccount.setTotal(finalAccount.getTotal().add(sum));
            return null;
        })
                .when(accountService).updateSum(account2, sum, Operation.DEBET);
        when(categoryDao.findByTitle("transfer")).thenReturn(Optional.of(transferCategory));
        when(transactionDao.save(any(Transaction.class))).then(
                (invocation) -> null
        );

        subj.commitMoneyTransaction(account.getId(), account2.getId(), sum);

        assertEquals(sum, entityManager.find(Account.class, account.getId()).getTotal());
        assertEquals(sum, entityManager.find(Account.class, account2.getId()).getTotal());
    }

    @Test
    public void commitTransaction_passUnknownAccountId_throwsAccountNotFoundException() {
        assertThrows(
                AccountNotFoundException.class,
                () -> subj.commitMoneyTransaction(100L, 101L, new BigDecimal("1000"))
        );
    }

    @Test
    public void commitTransaction_passZeroSum_throwsOperationFailedException() {
        assertThrows(
                OperationFailedException.class,
                () -> subj.commitMoneyTransaction(100L, 101L, BigDecimal.ZERO)
        );
    }

    @Test
    public void commitTransaction_notEnoughMoneyForTransfer_throwsAccountNotMatchException()
            throws AccountNotMatchException {
        Account account2 = new Account(
                "destination account",
                BigDecimal.ZERO,
                accountType,
                user
        );
        entityManager.persist(account);
        entityManager.persist(account2);

        Long fromId = account.getId();
        Long toId = account2.getId();
        BigDecimal sum = new BigDecimal("5000.00");

        when(accountService.findById(fromId)).thenReturn(account);
        when(accountService.findById(toId)).thenReturn(account2);
        when(categoryDao.findByTitle("transfer")).thenReturn(Optional.of(transferCategory));
        doThrow(AccountNotMatchException.class)
                .when(accountService).updateSum(account, sum, Operation.CREDIT);
        Account finalAccount = account2;

        assertThrows(
                AccountNotMatchException.class,
                () -> subj.commitMoneyTransaction(
                        account.getId(),
                        finalAccount.getId(),
                        sum)
        );
    }

    @Test
    public void commitTransaction_passSameIdForBothAccounts_throwsAccountNotMatchException() {
        assertThrows(
                AccountNotMatchException.class,
                () -> subj.commitMoneyTransaction(
                        account.getId(),
                        account.getId(),
                        new BigDecimal("1000")
                ));
    }

    @Test
    public void commitTransaction_transferCategoryNotFound_throwsCategoryNotFoundException() {
        Account account2 = new Account(
                "destination account",
                BigDecimal.ZERO,
                accountType,
                user
        );
        entityManager.persist(account);
        entityManager.persist(account2);

        Long fromId = account.getId();
        Long toId = account2.getId();
        BigDecimal sum = new BigDecimal("5000.00");

        when(accountService.findById(fromId)).thenReturn(account);
        when(accountService.findById(toId)).thenReturn(account2);
        doThrow(CategoryNotFoundException.class)
                .when(categoryDao).findByTitle("transfer");
        Account finalAccount = account2;

        assertThrows(
                CategoryNotFoundException.class,
                () -> subj.commitMoneyTransaction(
                        account.getId(),
                        finalAccount.getId(),
                        new BigDecimal("1000")
                ));
    }

}