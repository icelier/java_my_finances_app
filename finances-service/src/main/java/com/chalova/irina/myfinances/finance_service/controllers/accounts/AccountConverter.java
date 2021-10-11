package com.chalova.irina.myfinances.finance_service.controllers.accounts;

import com.chalova.irina.myfinances.commons_service.dto.AccountDto;
import com.chalova.irina.myfinances.commons_service.dto.DtoConverter;
import com.chalova.irina.myfinances.finance_service.dao.accounts.AccountTypeDao;
import com.chalova.irina.myfinances.finance_service.entities.accounts.Account;
import com.chalova.irina.myfinances.finance_service.entities.accounts.AccountType;
import com.chalova.irina.myfinances.finance_service.exceptions.accounts.AccountTypeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class AccountConverter implements
        DtoConverter<AccountDto, Account> {
    private final AccountTypeDao accountTypeDao;

    @Override
    public AccountDto convertDomainToDto(Account account) {
        return new AccountDto(
                account.getId(),
                account.getName(),
                account.getTotal().toString(),
                account.getType().getTitle()
        );
    }

    @Override
    public Account convertDomainFromDto(AccountDto accountDto) throws AccountTypeNotFoundException {
        AccountType accountType = accountTypeDao.findByTitle(accountDto.getAccountType()).orElse(null);
        if (accountType == null) {
            throw new AccountTypeNotFoundException("AccountType not found");
        }

        return new Account(
                accountDto.getId(),
                accountDto.getName(),
                new BigDecimal(accountDto.getTotal()),
                accountType,
                null);
    }
}
