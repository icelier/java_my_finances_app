package lessons.lesson_4_add_layers_and_factories.terminal_views;

import lessons.lesson_4_add_layers_and_factories.entities.finances.Account;
import lessons.lesson_4_add_layers_and_factories.entities.finances.Transaction;
import lessons.lesson_4_add_layers_and_factories.entities.users.UserProjection;
import lessons.lesson_4_add_layers_and_factories.services.RequestResult;
import lessons.lesson_4_add_layers_and_factories.services.finances.AccountService;
import lessons.lesson_4_add_layers_and_factories.services.finances.TransactionService;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class TransactionTerminal {
    private UtilTerminal utilTerminal;
    private AccountTerminal accountTerminal;
    private TransactionService transactionService;
    private PrintWriter printer;
    private Scanner scanner;

    public TransactionTerminal(TransactionService transactionService,
                               UtilTerminal utilTerminal,
                               AccountTerminal accountTerminal,
                               PrintWriter printer,
                               Scanner scanner) {
        this.transactionService = transactionService;
        this.utilTerminal = utilTerminal;
        this.accountTerminal = accountTerminal;
        this.printer = printer;
        this.scanner = scanner;
    }

    public List<Transaction> printAllTransactions(Long userId) throws Exception {
        RequestResult result = getUserTransactions(userId);
        if (result == RequestResult.SUCCESS) {
            printer.println("Requested transactions:");
            List<Transaction> userTransactions = (List<Transaction>)result.getData();
            int i = 0;
            for (Transaction transaction:
                    userTransactions) {
                printer.printf("%d. %s", ++i, transaction);
                printer.println();
            }
            return userTransactions;
        }
        if (result == RequestResult.FAIL) {
            printer.println(result.getMsg());
        }
        if (result == RequestResult.ERROR) {
            printer.println(result.getMsg());
        }

        return null;
    }

    private RequestResult getUserTransactions(Long userId) throws Exception {
        List<Transaction> userTransactions = null;
        try {
            userTransactions = transactionService.findAllByUserId(userId);
        } catch (SQLException e) {
            return RequestResult.ERROR
                    .setMsg("Failed to get user transactions from database")
                    .setData(e);
        }
        if (userTransactions.isEmpty()) {
            return RequestResult.FAIL
                    .setMsg("No user transactions registered");
        }

        return RequestResult.SUCCESS
                .setData(userTransactions);
    }

    public List<Transaction> printTodayTransactions(Long userId) throws Exception {
        RequestResult result = getTodayTransactions(userId);
        if (result == RequestResult.SUCCESS) {
            printer.println("Your today transactions:");
            List<Transaction> todayTransactions = (List<Transaction>)result.getData();
            int i = 0;
            for (Transaction transaction:
                    todayTransactions) {
                printer.printf("%d. %s", ++i, transaction);
                printer.println();
            }
            return todayTransactions;
        }
        if (result == RequestResult.FAIL) {
            printer.println(result.getMsg());
        }
        if (result == RequestResult.ERROR) {
            printer.println(result.getMsg());
        }

        return null;
    }

    public RequestResult getTodayTransactions(Long userId) throws Exception {
        List<Transaction> userTodayTransactions = null;
        try {
            userTodayTransactions = transactionService.findAllByUserIdToday(userId);
        } catch (SQLException e) {
            return RequestResult.ERROR
                    .setMsg("Failed to get user transactions from database")
                    .setData(e);
        }
        if (userTodayTransactions.isEmpty()) {
            return RequestResult.FAIL
                    .setMsg("No user transactions for today");
        }

        return RequestResult.SUCCESS
                .setData(userTodayTransactions);
    }

    public void makeTransaction(UserProjection user) throws Exception {
        List<Account> userAccounts = getAndPrintAllAccounts(user.getId());
        if (userAccounts == null) {
            printer.println("No accounts available");
            return;
        }

        if (userAccounts.size() <= 1) {
            printer.println("No accounts for transfer");
            return;
        }

        Account fromAccount = chooseWithdrawAccount(userAccounts);
        Account toAccount = chooseTransferAccount(userAccounts);

        if (fromAccount.equals(toAccount)) {
            printer.println("Origin and destination account match");
            printer.println("Transaction cannot be committed");
            return;
        }

        BigDecimal transactionSum = getSumForTransaction(fromAccount.getSum());

//        boolean moneyWithdrawn = withdrawMoney(withdrawAccount, transactionSum);
//        if (!moneyWithdrawn) {
//            printer.println("Sorry, transaction failed. Please try again later");
//            return;
//        }
        //        commitTransaction(
//                withdrawAccount,
//                transferAccount,
//                transactionSum);

//        transferMoney(transferAccount, transactionSum);

        transactionService.commitTransaction(
                fromAccount.getId(),
                toAccount.getId(),
                transactionSum
        );
        printer.println(transactionSum + " was transferred from account " + fromAccount.getName() +
                " to account " + toAccount.getName());
    }

    public List<Account> getAndPrintAllAccounts(Long userId) throws Exception {
        return accountTerminal.getAndPrintAllAccounts(userId);
    }

    public Account chooseWithdrawAccount(List<Account> userAccounts) {
        printer.println("Choose account to withdraw money for transaction");

        return chooseAccount(userAccounts);
    }

    public Account chooseTransferAccount(List<Account> userAccounts) {
        printer.println("Choose account to transfer money to");

        return chooseAccount(userAccounts);
    }

    private Account chooseAccount(List<Account> userAccounts) {
        int choice = utilTerminal.chooseAndValidate(userAccounts.size()) - 1;

        return userAccounts.get(choice);
    }

    public BigDecimal getSumForTransaction(BigDecimal limit) {
        printer.println("Type in sum to be withdrawn from origin account for transaction");

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
            }
            if (!limitValidated) {
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
