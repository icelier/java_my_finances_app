package com.chalova.irina.myfinances.finance_service.controllers.transactions;

import com.chalova.irina.myfinances.commons_service.dto.TransactionDto;
import com.chalova.irina.myfinances.commons_service.dto.TransferRequest;
import com.chalova.irina.myfinances.finance_service.entities.transactions.AccountTransaction;
import com.chalova.irina.myfinances.finance_service.services.transactions.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.chalova.irina.myfinances.commons_service.PathConstants.TRANSACTIONS_BASE_PATH;


@RequiredArgsConstructor
@RequestMapping(TRANSACTIONS_BASE_PATH)
@RestController
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionConverter converter;

    @PostMapping(
            path = "/commit")
    public List<TransactionDto> commitTransaction(@RequestBody TransferRequest transferRequest) {
        try {
            transactionService.commitMoneyTransaction(
                    transferRequest.getAccountFromId(),
                    transferRequest.getAccountToId(),
                    new BigDecimal(transferRequest.getSum())
            );

            List<AccountTransaction> accountTransactions = transactionService.findAllByAccountId(transferRequest.getAccountFromId());

            return accountTransactions.stream()
                    .map(converter::convertDomainToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }

    }

    @GetMapping(
            path = "/{accountId}")
    public List<TransactionDto> getAllTransactionsByAccountId(
            @PathVariable(value = "accountId") Long accountId
    ) {
        List<AccountTransaction> transactions = transactionService.findAllByAccountId(accountId);

        System.out.println("account transactions list: " + transactions);

        return transactions.stream()
                .map(converter::convertDomainToDto)
                .collect(Collectors.toList());
    }


}
