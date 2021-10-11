package com.chalova.irina.myfinances.finance_service.controllers.accounts;

import com.chalova.irina.myfinances.commons_service.dto.AccountTypeDto;
import com.chalova.irina.myfinances.commons_service.dto.DtoConverter;
import com.chalova.irina.myfinances.commons_service.exceptions.not_found_exception.DataNotFoundException;
import com.chalova.irina.myfinances.finance_service.entities.accounts.AccountType;

public class AccountTypeConverter implements DtoConverter<AccountTypeDto, AccountType> {
    @Override
    public AccountType convertDomainFromDto(AccountTypeDto accountTypeDto) throws DataNotFoundException {
        return new AccountType(accountTypeDto.getTitle());
    }

    @Override
    public AccountTypeDto convertDomainToDto(AccountType accountType) {
        return new AccountTypeDto(accountType.getTitle());
    }
}
