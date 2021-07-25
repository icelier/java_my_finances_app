package lessons.lesson_4_add_layers_and_factories.singleton_factories;

import lessons.lesson_4_add_layers_and_factories.terminal_views.AccountTerminal;
import lessons.lesson_4_add_layers_and_factories.terminal_views.TransactionTerminal;
import lessons.lesson_4_add_layers_and_factories.terminal_views.UserTerminal;
import lessons.lesson_4_add_layers_and_factories.terminal_views.UtilTerminal;

public class TerminalFactory {
    // finances
    private static AccountTerminal accountTerminal;

    public static AccountTerminal getAccountTerminal() throws Exception {
        if (accountTerminal == null) {
            accountTerminal = new AccountTerminal(
                    ServiceFactory.getAccountService(),
                    UtilsFactory.getPrinter(),
                    getUtilTerminal()
                    );
        }
        return accountTerminal;
    }

    private static TransactionTerminal transactionTerminal;

    public static TransactionTerminal getTransactionTerminal() throws Exception {
        if (transactionTerminal == null) {
            transactionTerminal = new TransactionTerminal(
                    ServiceFactory.getTransactionService(),
                    getUtilTerminal(),
                    getAccountTerminal(),
                    UtilsFactory.getPrinter(),
                    UtilsFactory.getScanner());
        }
        return transactionTerminal;
    }

    // users
    private static UserTerminal userTerminal;

    public static UserTerminal getUserTerminal() throws Exception {
        if (userTerminal == null) {
            userTerminal = new UserTerminal(
                    ServiceFactory.getUserService(),
                    UtilsFactory.getPrinter(),
                    UtilsFactory.getScanner()
            );
        }
        return userTerminal;
    }

    // utils
    private static UtilTerminal utilTerminal;

    public static UtilTerminal getUtilTerminal()  {
        if (utilTerminal == null) {
            utilTerminal = new UtilTerminal(
                    UtilsFactory.getPrinter(),
                    UtilsFactory.getScanner()
            );
        }
        return utilTerminal;
    }
}
