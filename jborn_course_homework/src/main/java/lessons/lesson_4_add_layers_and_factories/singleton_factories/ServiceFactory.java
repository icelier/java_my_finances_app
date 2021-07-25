package lessons.lesson_4_add_layers_and_factories.singleton_factories;

import lessons.lesson_4_add_layers_and_factories.services.finances.AccountService;
import lessons.lesson_4_add_layers_and_factories.services.finances.CategoryService;
import lessons.lesson_4_add_layers_and_factories.services.finances.TransactionService;
import lessons.lesson_4_add_layers_and_factories.services.users.UserService;

public class ServiceFactory {
    // users
    private static UserService userService;

    public static UserService getUserService() throws Exception {
        if (userService == null) {
            userService = new UserService(DaoFactory.getUserDao());
        }
        return userService;
    }

    // finances
    private static AccountService accountService;

    public static AccountService getAccountService() throws Exception {
        if (accountService == null) {
            accountService = new AccountService(DaoFactory.getAccountDao());
        }
        return accountService;
    }

    private static TransactionService transactionService;

    public static TransactionService getTransactionService() throws Exception {
        if (transactionService == null) {
            transactionService = new TransactionService(
                    DaoFactory.getTransactionDao(),
                    ServiceFactory.getCategoryService(),
                    ServiceFactory.getAccountService(),
                    DaoFactory.getDataSource());
        }
        return transactionService;
    }

    private static CategoryService categoryService;

    public static CategoryService getCategoryService() throws Exception {
        if (categoryService == null) {
            categoryService = new CategoryService(DaoFactory.getCategoryDao());
        }
        return categoryService;
    }
}
