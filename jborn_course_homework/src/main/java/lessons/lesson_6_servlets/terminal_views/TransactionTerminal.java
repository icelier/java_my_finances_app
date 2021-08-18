package lessons.lesson_6_servlets.terminal_views;

import lessons.lesson_6_servlets.entities.finances.Account;
import lessons.lesson_6_servlets.entities.finances.Transaction;
import lessons.lesson_6_servlets.entities.users.UserLoginProjection;
import lessons.lesson_6_servlets.exceptions.already_exists_exception.TransactionAlreadyExistsException;
import lessons.lesson_6_servlets.exceptions.not_found_exception.AccountNotFoundException;
import lessons.lesson_6_servlets.exceptions.not_found_exception.CategoryNotFoundException;
import lessons.lesson_6_servlets.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_6_servlets.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_6_servlets.services.finances.TransactionService;
import org.springframework.stereotype.Controller;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

@Controller
public class TransactionTerminal {
    private UtilTerminal utilTerminal;
    private AccountTerminal accountTerminal;
    private TransactionService transactionService;
    private PrintWriter printer;
    private Scanner scanner;

    public TransactionTerminal(TransactionService transactionService,
                               AccountTerminal accountTerminal,
                               UtilTerminal utilTerminal,
                               PrintWriter printer,
                               Scanner scanner) {
        this.transactionService = transactionService;
        this.accountTerminal = accountTerminal;
        this.utilTerminal = utilTerminal;
        this.printer = printer;
        this.scanner = scanner;
    }


    void getAndPrintAllTransactions(Long userId) throws SQLException {
        List<Transaction> userTransactions = transactionService.findAllByUserId(userId);
        if (!userTransactions.isEmpty()) {
            int i = 0;
            for (Transaction transaction: userTransactions) {
                printer.printf("%d. %s", ++i, transaction);
                printer.println();
            }
        } else {
            printer.println("No user transactions found");
        }
    }

    void getAndPrintTodayTransactions(Long userId) throws SQLException {
        List<Transaction> userTodayTransactions = transactionService.findAllByUserIdToday(userId);
        if (!userTodayTransactions.isEmpty()) {
            int i = 0;
            for (Transaction transaction: userTodayTransactions) {
                printer.printf("%d. %s", ++i, transaction);
                printer.println();
            }
        } else {
            printer.println("No user transactions for today found");
        }
    }

    /**
     *Commits money transfer from one user account to another in one transaction, updates total of two accounts and
     * inserts two new transactions. If failed at any stage, whole transfer transaction is rollbacked
     * @param user login projection to perform transfer for
     * @throws AccountNotMatchException if there are not enough user accounts number (no user accounts at all or only one),
     * if origin and destination accounts is the same account
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     */
    public void makeTransaction(UserLoginProjection user) throws AccountNotMatchException, SQLException, OperationFailedException {
        List<Account> userAccounts = getAndPrintAllAccounts(user.getId());

        if (userAccounts.size() <= 1) {
            throw new AccountNotMatchException("Not enough accounts for transfer");
        }

        int fromAccountNumber = chooseWithdrawAccount(userAccounts.size());
        int toAccountNumber = chooseTransferAccount(userAccounts.size());

        if (userAccounts.get(fromAccountNumber).getId().equals(userAccounts.get(toAccountNumber).getId())) {
            throw new AccountNotMatchException("Origin and destination accounts id match");
        }

        BigDecimal transactionSum = getSumForTransaction(userAccounts.get(fromAccountNumber).getSum());

        try {
            transactionService.commitTransaction(
                    userAccounts.get(fromAccountNumber).getId(),
                    userAccounts.get(toAccountNumber).getId(),
                    transactionSum
            );
        } catch (AccountNotFoundException | SQLException | TransactionAlreadyExistsException | CategoryNotFoundException e) {
            throw new OperationFailedException(e.getLocalizedMessage());
        }
        printer.println(transactionSum + " was transferred from account " + userAccounts.get(fromAccountNumber).getName() +
                " to account " + userAccounts.get(toAccountNumber).getName());
    }

    public List<Account> getAndPrintAllAccounts(Long userId) throws SQLException {
        return accountTerminal.getAndPrintAllAccounts(userId);
    }

    public int chooseWithdrawAccount(int accountsNumber) {
        printer.println("Choose account to withdraw money for transaction");

        return chooseAccount(accountsNumber);
    }

    public int chooseTransferAccount(int accountsNumber) {
        printer.println("Choose account to transfer money to");

        return chooseAccount(accountsNumber);
    }

    private int chooseAccount(int accountsNumber) {
        int choice = utilTerminal.chooseAndValidate(accountsNumber);

        return choice - 1;
    }

    public BigDecimal getSumForTransaction(BigDecimal limit) {
        printer.println("Enter sum to be withdrawn from origin account for transaction");

        return getSumAndValidate(limit);

    }

    public BigDecimal getSumAndValidate(BigDecimal validationLimit) {
        BigDecimal sum = BigDecimal.ZERO;
        boolean sumValidated = false;

        printer.println("Please enter sum");
        while (!sumValidated) {
            sum = getSumInput();
            boolean zeroAndNegativeValidated = validateSumForZeroAndNegative(sum);
            boolean limitValidated = validateForLimit(sum, validationLimit);
            sumValidated = zeroAndNegativeValidated && limitValidated;
            if (sumValidated) {
                break;
            }
            if (!zeroAndNegativeValidated) {
                printer.println("Please enter correct sum for transfer");
            } else if (!limitValidated) {
                printer.println("Sum beyond limit. Please enter correct sum");
            }
        }

        return sum;
    }

    private BigDecimal getSumInput() {
        BigDecimal sum = BigDecimal.ZERO;
        while (scanner.hasNext()) {
            if (scanner.hasNextBigDecimal()) {
                sum = scanner.nextBigDecimal();
                break;
            }
        }
        return sum;
    }

    private boolean validateSumForZeroAndNegative(BigDecimal sum) {
        return sum.signum() > 0;
    }
    private boolean validateForLimit(BigDecimal sum, BigDecimal limit) {
        return sum.compareTo(limit) <= 0;
    }
}
