package lessons.lesson_4_add_layers_and_factories.terminal_views;

import lessons.lesson_4_add_layers_and_factories.entities.finances.Account;
import lessons.lesson_4_add_layers_and_factories.services.finances.AccountService;
import lessons.lesson_4_add_layers_and_factories.services.RequestResult;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class AccountTerminal {
    private UtilTerminal utilTerminal;
    private AccountService accountService;
    private PrintWriter printer;

    public AccountTerminal(AccountService accountService, PrintWriter printer, UtilTerminal utilTerminal) {
        this.accountService = accountService;
        this.printer = printer;
        this.utilTerminal = utilTerminal;
    }

    public List<Account> getAndPrintAllAccounts(Long userId) throws Exception {
        List<Account> userAccounts = accountService.findAllByUserId(userId);
//        RequestResult result = getUserAccounts(userId);
//
//        if (result == RequestResult.SUCCESS) {
//            printer.println("Your current accounts:");
//            List<Account> userAccounts = (List<Account>)result.getData();
            int i = 0;
            for (Account account:
                    userAccounts) {
                printer.printf("%d. %s", ++i, account);
                printer.println();
            }
            return userAccounts;
//        }
//        if (result == RequestResult.FAIL) {
//            printer.println(result.getMsg());
//        }
//        if (result == RequestResult.ERROR) {
//            printer.println(result.getMsg());
//        }
//
//        return null;
    }

    private RequestResult getUserAccounts(Long userId) {
        List<Account> userAccounts = null;
        try {
            userAccounts = accountService.findAllByUserId(userId);
        } catch (SQLException e) {
            return RequestResult.ERROR
                    .setMsg("Failed to get user accounts from database")
                    .setData(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (userAccounts.isEmpty()) {
            return RequestResult.FAIL
                    .setMsg("No user accounts registered");
        }

        return RequestResult.SUCCESS
                .setData(userAccounts);
    }
}
