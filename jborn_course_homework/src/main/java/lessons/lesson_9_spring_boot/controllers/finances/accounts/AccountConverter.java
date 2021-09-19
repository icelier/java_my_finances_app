package lessons.lesson_9_spring_boot.controllers.finances.accounts;

import lessons.lesson_9_spring_boot.controllers.DomainConverter;
import lessons.lesson_9_spring_boot.controllers.finances.transactions.TransactionResponse;
import lessons.lesson_9_spring_boot.controllers.finances.transactions.TransactionConverter;
import lessons.lesson_9_spring_boot.dao.finances.AccountTypeDao;
import lessons.lesson_9_spring_boot.entities.finances.Account;
import lessons.lesson_9_spring_boot.entities.finances.AccountType;
import lessons.lesson_9_spring_boot.entities.finances.Transaction;
import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.AccountTypeNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_9_spring_boot.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AccountConverter implements
        DomainConverter<AccountRequest, Account, AccountResponse> {
    private final AccountTypeDao accountTypeDao;
    private final UserService userService;
    private final TransactionConverter transactionConverter;

    @Override
    public AccountResponse convertDomainToResponse(Account account) {
        List<Transaction> transactions = account.getTransactions();
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        for (Transaction transaction : transactions) {
            TransactionResponse transactionResponse =
                    transactionConverter.convertDomainToResponse(transaction);
            transactionResponses.add(transactionResponse);
        }
        return new AccountResponse(
                account.getName(),
                account.getTotal().toString(),
                account.getType().getTitle(),
                account.getUser().getUserName(),
                transactionResponses
        );
    }

    @Override
    public Account convertDomainFromRequest(AccountRequest request) throws UserNotFoundException,
            AccountTypeNotFoundException {
        UserEntity user = userService.findByUserName(request.getUserName());
        if (user == null) {
            throw new UserNotFoundException("User name not found");
        }
        AccountType accountType = accountTypeDao.findByTitle(request.getAccountType()).orElse(null);
        if (accountType == null) {
            throw new AccountTypeNotFoundException("AccountType not found");
        }

        return new Account(request.getName(),
                new BigDecimal(request.getTotal()),
                accountType,
                user);
    }
}
