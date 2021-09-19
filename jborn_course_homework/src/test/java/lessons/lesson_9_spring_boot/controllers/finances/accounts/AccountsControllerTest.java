package lessons.lesson_9_spring_boot.controllers.finances.accounts;

import lessons.lesson_9_spring_boot.entities.finances.Account;
import lessons.lesson_9_spring_boot.entities.finances.AccountType;
import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.AccountTypeNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_9_spring_boot.services.finances.AccountService;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountsController.class)
@RunWith(SpringRunner.class)
public class AccountsControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockBean private AccountService accountService;
    @MockBean private AccountConverter converter;

    private UserEntity user;
    private Account account;
    private AccountRequest accountRequest;
    private String accountName = "account name";
    private String accountTotal = "1000.00";
    private String accountType = "salary card";
    private String userName = "daddy";

    @Before
    public void setUp() throws Exception {
        user = new UserEntity(1L, userName, "", "123","sokol@gmail.com", 100);
        accountRequest = new AccountRequest(
                accountName,
                accountTotal,
                accountType,
                userName
        );
        account = new Account(
                accountName,
                new BigDecimal(accountTotal),
                new AccountType(2L, accountType),
                user
        );
    }

    @Test
    public void addAccount_getStatusOk() throws Exception {
        when(converter.convertDomainFromRequest(accountRequest)).thenReturn(account);
        when(accountService.insert(account)).thenReturn(account);

        mockMvc.perform(
                post("/my-finances/accounts/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"name\": \"account name\",\n" +
                                "  \"total\": \"1000.00\",\n" +
                                "  \"accountType\": \"salary card\",\n" +
                                "  \"userName\": \"daddy\"\n" +
                                "}")
        ).andExpect(status().isOk());
    }

    @Test
    public void addAccount_passBlankData_getBadRequestStatus() throws Exception {
        mockMvc.perform(
                post("/my-finances/accounts/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"name\": \"\",\n" +
                                "  \"total\": \"1000.00\",\n" +
                                "  \"accountType\": \"salary card\",\n" +
                                "  \"userName\": \"daddy\"\n" +
                                "}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void addAccount_passNotEnoughData_getBadRequestStatus() throws Exception {
        mockMvc.perform(
                post("/my-finances/accounts/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"total\": \"1000.00\",\n" +
                                "  \"accountType\": \"salary card\",\n" +
                                "  \"userName\": \"daddy\"\n" +
                                "}")
        ).andExpect(status().isBadRequest());

    }

    @Test
    public void addAccount_passIncorrectUserName_throwsUserNotFoundException_getNotFoundStatus() throws Exception {
        accountRequest.setUserName("some userName");
        when(converter.convertDomainFromRequest(accountRequest)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                post("/my-finances/accounts/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"name\": \"account name\",\n" +
                                "  \"total\": \"1000.00\",\n" +
                                "  \"accountType\": \"salary card\",\n" +
                                "  \"userName\": \"some userName\"\n" +
                                "}")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void addAccount_passIncorrectAccountType_throwsAccountTypeNotFoundException_getNotFoundStatus() throws Exception {
        accountRequest.setAccountType("some accountType");
        when(converter.convertDomainFromRequest(accountRequest)).thenThrow(AccountTypeNotFoundException.class);

        mockMvc.perform(
                post("/my-finances/accounts/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"name\": \"account name\",\n" +
                                "  \"total\": \"1000.00\",\n" +
                                "  \"accountType\": \"some accountType\",\n" +
                                "  \"userName\": \"daddy\"\n" +
                                "}")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void addAccount_passAlreadyExistingAccount_throwsAccountAlreadyExistsException_getBadRequestStatus() throws Exception {
        accountRequest.setName("father main salary card");
        account.setName("father main salary card");
        accountRequest.setTotal("150000");
        account.setTotal(new BigDecimal("150000"));

        when(converter.convertDomainFromRequest(accountRequest)).thenReturn(account);
        when(accountService.insert(account)).thenThrow(AccountAlreadyExistsException.class);

        mockMvc.perform(
                post("/my-finances/accounts/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"name\": \"" + "father main salary card" + "\",\n" +
                                "  \"total\": \"150000\",\n" +
                                "  \"accountType\": \"salary card\",\n" +
                                "  \"userName\": \"daddy\"\n" +
                                "}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void getAllAccountsByUserId_getStatusOk() throws Exception {
        List<Account> accountList = new ArrayList<>();
        accountList.add(account);

        when(accountService.findAllByUserId(1L)).thenReturn(accountList);
        when(converter.convertDomainToResponse(account)).thenReturn(
                new AccountResponse(
                        accountRequest.getName(),
                        accountRequest.getTotal(),
                        accountRequest.getAccountType(),
                        accountRequest.getUserName(),
                        Collections.emptyList()
                )
        );

        mockMvc.perform(
                get("/my-finances/accounts/")
                        .param("userId", "1")
        ).andExpect(status().isOk());
    }

    @Test
    public void updateAccount_getStatusOk() throws Exception {
        when(accountService.findById(1L)).then( (invocationOnMock) -> {
                    account.setId(1L);
                    account.setName("father main salary card");
                    account.setTotal(new BigDecimal("150000"));
                    return account;
                }
        );
        Account updateData = new Account(
                accountName,
                new BigDecimal(accountTotal),
                new AccountType(2L, accountType),
                user
        );

        when(converter.convertDomainFromRequest(accountRequest)).thenReturn(updateData);

        when(accountService.update(1L, updateData)).then((invocationOnMock) -> {
            account.setName(accountName);
            account.setTotal(new BigDecimal(accountTotal));
            return account;
        });

        mockMvc.perform(
                post("/my-finances/accounts/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"name\": \"account name\",\n" +
                                "  \"total\": \"1000.00\",\n" +
                                "  \"accountType\": \"salary card\",\n" +
                                "  \"userName\": \"daddy\"\n" +
                                "}")
                .param("id", "1")
        ).andExpect(status().isOk());
    }

    @Test
    public void updateAccount_passBlankData_getBadRequestStatus() throws Exception {
        mockMvc.perform(
                post("/my-finances/accounts/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"name\": \"\",\n" +
                                "  \"total\": \"1000.00\",\n" +
                                "  \"accountType\": \"salary card\",\n" +
                                "  \"userName\": \"daddy\"\n" +
                                "}")
                        .param("id", "1")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void updateAccount_passNotEnoughData_getBadRequestStatus() throws Exception {
        mockMvc.perform(
                post("/my-finances/accounts/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"total\": \"1000.00\",\n" +
                                "  \"accountType\": \"salary card\",\n" +
                                "  \"userName\": \"daddy\"\n" +
                                "}")
                        .param("id", "1")
        ).andExpect(status().isBadRequest());

    }

    @Test
    public void updateAccount_passIncorrectAccountId_getNotFoundStatus() throws Exception {
        when(accountService.findById(100L)).thenReturn(null);

        mockMvc.perform(
                post("/my-finances/accounts/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"name\": \"account name\",\n" +
                                "  \"total\": \"1000.00\",\n" +
                                "  \"accountType\": \"salary card\",\n" +
                                "  \"userName\": \"daddy\"\n" +
                                "}")
                        .param("id", "100")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void updateAccount_passIncorrectUserName_throwsUserNotFoundException_getNotFoundStatus() throws Exception {
        when(accountService.findById(1L)).then( (invocationOnMock) -> {
                    account.setId(1L);
                    account.setName("father main salary card");
                    account.setTotal(new BigDecimal("150000"));
                    return account;
                }
        );

        accountRequest.setUserName("some userName");
        when(converter.convertDomainFromRequest(accountRequest)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                post("/my-finances/accounts/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"name\": \"account name\",\n" +
                                "  \"total\": \"1000.00\",\n" +
                                "  \"accountType\": \"salary card\",\n" +
                                "  \"userName\": \"some userName\"\n" +
                                "}")
                        .param("id", "1")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void updateAccount_passIncorrectAccountType_throwsAccountTypeNotFoundException_getNotFoundStatus() throws Exception {
        when(accountService.findById(1L)).then( (invocationOnMock) -> {
                    account.setId(1L);
                    account.setName("father main salary card");
                    account.setTotal(new BigDecimal("150000"));
                    return account;
                }
        );

        accountRequest.setAccountType("some accountType");
        when(converter.convertDomainFromRequest(accountRequest)).thenThrow(AccountTypeNotFoundException.class);

        mockMvc.perform(
                post("/my-finances/accounts/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"name\": \"account name\",\n" +
                                "  \"total\": \"1000.00\",\n" +
                                "  \"accountType\": \"some accountType\",\n" +
                                "  \"userName\": \"daddy\"\n" +
                                "}")
                        .param("id", "1")
        ).andExpect(status().isNotFound());
    }
}