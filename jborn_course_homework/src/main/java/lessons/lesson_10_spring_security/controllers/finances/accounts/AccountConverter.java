package lessons.lesson_10_spring_security.controllers.finances.accounts;

import lessons.lesson_10_spring_security.controllers.DomainConverter;
import lessons.lesson_10_spring_security.controllers.finances.transactions.TransactionConverter;
import lessons.lesson_10_spring_security.dao.finances.AccountTypeDao;
import lessons.lesson_10_spring_security.entities.finances.Account;
import lessons.lesson_10_spring_security.entities.finances.AccountType;
import lessons.lesson_10_spring_security.entities.users.UserEntity;
import lessons.lesson_10_spring_security.exceptions.not_found_exception.AccountTypeNotFoundException;
import lessons.lesson_10_spring_security.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_10_spring_security.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class AccountConverter implements
        DomainConverter<AccountRequest, Account, AccountResponse> {
    private final AccountTypeDao accountTypeDao;
    private final UserService userService;
    private final TransactionConverter transactionConverter;

    @Override
    public AccountResponse convertDomainToResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getTotal().toString(),
                account.getType().getTitle()
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

        return new Account(
                request.getName(),
                new BigDecimal(request.getTotal()),
                accountType,
                user);
    }
}
