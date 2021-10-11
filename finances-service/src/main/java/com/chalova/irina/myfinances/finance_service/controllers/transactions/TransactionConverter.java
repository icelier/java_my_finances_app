package com.chalova.irina.myfinances.finance_service.controllers.transactions;

import com.chalova.irina.myfinances.commons_service.dto.DtoConverter;
import com.chalova.irina.myfinances.commons_service.dto.TransactionDto;
import com.chalova.irina.myfinances.finance_service.dao.transactions.CategoryDao;
import com.chalova.irina.myfinances.finance_service.entities.accounts.Account;
import com.chalova.irina.myfinances.finance_service.entities.transactions.Category;
import com.chalova.irina.myfinances.finance_service.entities.transactions.Operation;
import com.chalova.irina.myfinances.finance_service.entities.transactions.AccountTransaction;
import com.chalova.irina.myfinances.finance_service.exceptions.accounts.AccountNotFoundException;
import com.chalova.irina.myfinances.finance_service.exceptions.transactions.CategoryNotFoundException;
import com.chalova.irina.myfinances.finance_service.services.accounts.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class TransactionConverter implements
        DtoConverter<TransactionDto, AccountTransaction>  {
    private final AccountService accountService;
    private final CategoryDao categoryDao;

    @Override
    public TransactionDto convertDomainToDto(AccountTransaction transaction){

        return new TransactionDto(
                transaction.getSum().toString(),
                transaction.getOperation().name(),
                transaction.getTimestamp().toString(),
                transaction.getAccount().getId(),
                transaction.getCategory().getTitle()
        );
    }

    @Override
    public AccountTransaction convertDomainFromDto(TransactionDto transactionDto) {
        Category category = categoryDao.findByTitle(transactionDto.getCategoryName()).orElse(null);
        if (category == null) {
            throw new CategoryNotFoundException("Transaction category not found");
        }
        Account account = accountService.findById(transactionDto.getAccountId());
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }

        return new AccountTransaction(
                new BigDecimal(transactionDto.getSum()),
                Operation.valueOf(transactionDto.getOperation()),
                account,
                category
        );
    }
}
