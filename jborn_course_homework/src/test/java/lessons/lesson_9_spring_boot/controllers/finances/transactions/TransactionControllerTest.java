package lessons.lesson_9_spring_boot.controllers.finances.transactions;

import lessons.lesson_9_spring_boot.entities.finances.*;
import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_9_spring_boot.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_9_spring_boot.services.finances.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@RunWith(SpringRunner.class)
public class TransactionControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockBean private TransactionService transactionService;
    @MockBean private TransactionConverter converter;

    private Transaction transaction;
    private TransactionRequest transactionRequest;
    private UserEntity user;
    private Account fromAccount;
    private Account toAccount;
    private Category transferCategory;

    @Before
    public void setUp() throws Exception {
        user = new UserEntity(2L, "mommy", "", "456","mom@gmail.com", 100);
        fromAccount = new Account(
                2L,
                "mother main salary card",
                new BigDecimal("26500"),
                new AccountType(2L, "salary card"),
                user
        );
        toAccount = new Account(
                3L,
                "mother credit card",
                new BigDecimal("110000"),
                new AccountType(3L, "credit card"),
                user
        );

        transferCategory = new Category(2L, "transfer");
        transaction = new Transaction(
                new BigDecimal("-1000"),
                Operation.CREDIT,
                fromAccount,
                transferCategory
        );
        transaction.setTimestamp(Instant.now());

        transactionRequest = new TransactionRequest(
                "1000",
                2L,
                3L
        );
    }

    @Test
    public void commitTransaction_getStatusOk() throws Exception {
        doNothing().when(transactionService).commitMoneyTransaction(3L, 2L, new BigDecimal("1000"));

        mockMvc.perform(
                post("/my-finances/transactions/commit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"sum\": \"1000\",\n" +
                                "  \"accountFromId\": \"3\",\n" +
                                "  \"accountToId\": \"2\"\n" +
                                "}")
        ).andExpect(status().isOk());
    }

    @Test
    public void commit_passBlankData_getBadRequestStatus() throws Exception {
        mockMvc.perform(
                post("/my-finances/transactions/commit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"sum\": \"\",\n" +
                                "  \"accountFromId\": \"3\",\n" +
                                "  \"accountToId\": \"2\"\n" +
                                "}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void commit_passNotEnoughData_getBadRequestStatus() throws Exception {
        mockMvc.perform(
                post("/my-finances/transactions/commit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"accountFromId\": \"3\",\n" +
                                "  \"accountToId\": \"2\"\n" +
                                "}")
        ).andExpect(status().isBadRequest());

    }

    @Test
    public void commitTransaction_throwCategoryNotFoundException_getInternalServerErrorStatus() throws Exception {
        doThrow(CategoryNotFoundException.class)
                .when(transactionService).commitMoneyTransaction(3L, 2L, new BigDecimal("1000"));

        mockMvc.perform(
                post("/my-finances/transactions/commit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"sum\": \"1000\",\n" +
                                "  \"accountFromId\": \"3\",\n" +
                                "  \"accountToId\": \"2\"\n" +
                                "}")
        ).andExpect(status().isInternalServerError());
    }

    @Test
    public void commitTransaction_passNegativeSum_throwOperationFailedException_getInternalServerErrorStatus() throws Exception {
        doThrow(OperationFailedException.class)
                .when(transactionService).commitMoneyTransaction(3L, 2L, new BigDecimal("-1000"));

        mockMvc.perform(
                post("/my-finances/transactions/commit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"sum\": \"-1000\",\n" +
                                "  \"accountFromId\": \"3\",\n" +
                                "  \"accountToId\": \"2\"\n" +
                                "}")
        ).andExpect(status().isInternalServerError());
    }

    @Test
    public void commitTransaction_passIncorrectAccountId_throwAccountNotFoundException_getBadRequestStatus() throws Exception {
        doThrow(AccountNotFoundException.class)
                .when(transactionService).commitMoneyTransaction(3000L, 2L, new BigDecimal("1000"));

        mockMvc.perform(
                post("/my-finances/transactions/commit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"sum\": \"1000\",\n" +
                                "  \"accountFromId\": \"3000\",\n" +
                                "  \"accountToId\": \"2\"\n" +
                                "}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void commitTransaction_passSameFromAndToAccountsId_throwAccountNotMatchException_getBadRequestStatus() throws Exception {
        doThrow(AccountNotMatchException.class)
                .when(transactionService).commitMoneyTransaction(3L, 3L, new BigDecimal("1000"));

        mockMvc.perform(
                post("/my-finances/transactions/commit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"sum\": \"1000\",\n" +
                                "  \"accountFromId\": \"3\",\n" +
                                "  \"accountToId\": \"3\"\n" +
                                "}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void commitTransaction_notEnoughMoneyForTransfer_throwAccountNotMatchException_getBadRequestStatus() throws Exception {
        doThrow(AccountNotMatchException.class)
                .when(transactionService).commitMoneyTransaction(3L, 2L, new BigDecimal("1000000000"));

        mockMvc.perform(
                post("/my-finances/transactions/commit")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"sum\": \"1000000000\",\n" +
                                "  \"accountFromId\": \"3\",\n" +
                                "  \"accountToId\": \"2\"\n" +
                                "}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void getAllTransactionsByUserId_getStatusOk() throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        when(transactionService.findAllByUserId(1L)).thenReturn(transactions);
        when(converter.convertDomainToResponse(transaction)).thenReturn(
                new TransactionResponse(
                        transaction.getSum().toString(),
                        transaction.getOperation().name(),
                        transaction.getTimestamp().toString(),
                        transaction.getAccount().getName(),
                        transaction.getCategory().getTitle()
                )
        );

        mockMvc.perform(
                get("/my-finances/transactions/{userId}", 1L)
        ).andExpect(status().isOk());
    }
}