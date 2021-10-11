package com.chalova.irina.myfinances.finance_service.controllers.accounts;

import com.chalova.irina.myfinances.commons_service.dto.AccountDto;
import com.chalova.irina.myfinances.finance_service.dao.accounts.AccountTypeDao;
import com.chalova.irina.myfinances.finance_service.entities.accounts.Account;
import com.chalova.irina.myfinances.finance_service.entities.accounts.AccountType;
import com.chalova.irina.myfinances.finance_service.exceptions.accounts.AccountAlreadyExistsException;
import com.chalova.irina.myfinances.finance_service.exceptions.accounts.AccountNotFoundException;
import com.chalova.irina.myfinances.finance_service.exceptions.accounts.AccountTypeNotFoundException;
import com.chalova.irina.myfinances.finance_service.services.accounts.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.chalova.irina.myfinances.commons_service.PathConstants.ACCOUNTS_BASE_PATH;


@RequiredArgsConstructor
@RequestMapping(ACCOUNTS_BASE_PATH)
@RestController
public class AccountsController {

    private final AccountService accountService;
    private final AccountConverter converter;
    private final AccountTypeDao accountTypeDao;

    @GetMapping()
    public List<AccountDto> getAllAccountsByUserName(@AuthenticationPrincipal Jwt jwt) {
        System.out.println("jwt = " + jwt.toString());
        String username = jwt.getClaim("user_name").toString();
        System.out.println("username = " + username);
        List<Account> userAccounts = accountService.findAllByUserName(username);

        return userAccounts.stream()
                .map(converter::convertDomainToDto)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/new_account")
    public List<AccountType> getAddAccountPage() {
        return accountTypeDao.findAll();
    }

    @PostMapping(
            path = "/add")
    public List<AccountDto> addAccount(@AuthenticationPrincipal Jwt jwt,
                                       @RequestBody AccountDto newAccount) {
        String username = jwt.getClaim("user_name").toString();

        Account account;
        try {
            account = converter.convertDomainFromDto(newAccount);
            account.setUserName(username);
        } catch (AccountTypeNotFoundException e) {
            return Collections.emptyList();
        }

        try {
            accountService.insert(account);

            return accountService.findAllByUserName(username).stream()
                    .map(converter::convertDomainToDto)
                    .collect(Collectors.toList());
        } catch (AccountAlreadyExistsException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @PostMapping(
            path = "/update")
    public List<AccountDto> updateAccount(@AuthenticationPrincipal Jwt jwt,
                                          @RequestBody AccountDto accountDto) {
        String username = jwt.getClaim("user_name");

        Account updateData = null;
        try {
            updateData = converter.convertDomainFromDto(accountDto);
        } catch (AccountTypeNotFoundException e) {
            return Collections.emptyList();
        }

        try {
            accountService.update(accountDto.getId(), updateData);
            List<Account> accounts = accountService.findAllByUserName(username);

            List<AccountDto> accountDtoList = accounts.stream()
                    .map(converter::convertDomainToDto)
                    .collect(Collectors.toList());
            System.out.println("account dto list: " + accountDtoList);
            return accountDtoList;
        } catch (AccountNotFoundException e) {
            return Collections.emptyList();
        }
    }

    @PostMapping(
            path = "/delete"
    )
    public List<AccountDto> deleteAccount(@RequestParam(name = "accountId") Long id) {
        Account accountFromDb = accountService.findById(id);
        if (accountFromDb == null) {
            return Collections.emptyList();
        }
        String userName = accountFromDb.getUserName();

        accountService.delete(accountFromDb);

        List<Account> accounts = accountService.findAllByUserName(userName);

        return accounts.stream()
                .map(converter::convertDomainToDto)
                .collect(Collectors.toList());
    }
}
