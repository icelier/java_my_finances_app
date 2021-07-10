package lessons.lesson_4.singleton_factories;

import lessons.lesson_4.services.finances.AccountServiceImpl;
import lessons.lesson_4.services.finances.TransactionServiceImpl;
import lessons.lesson_4.services.users.UserServiceImpl;

import java.sql.SQLException;

public class ServiceFactory {
    // users
    private static UserServiceImpl userService;

    public static UserServiceImpl getUserService() throws SQLException {
        if (userService == null) {
            userService = new UserServiceImpl(DaoFactory.getUserDao());
        }
        return userService;
    }

    // finances
    private static AccountServiceImpl accountService;

    public static AccountServiceImpl getAccountService() throws SQLException {
        if (accountService == null) {
            accountService = new AccountServiceImpl(DaoFactory.getAccountDao());
        }
        return accountService;
    }

    private static TransactionServiceImpl transactionService;

    public static TransactionServiceImpl getTransactionService() throws SQLException {
        if (transactionService == null) {
            transactionService = new TransactionServiceImpl(DaoFactory.getTransactionDao());
        }
        return transactionService;
    }
}
