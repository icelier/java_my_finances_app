package lessons.lesson_5_spring.terminal_views;

import lessons.lesson_5_spring.entities.finances.Account;
import lessons.lesson_5_spring.services.RequestResult;
import lessons.lesson_5_spring.services.finances.AccountService;
import org.springframework.stereotype.Controller;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@Controller
public class AccountTerminal {
    private AccountService accountService;
    private PrintWriter printer;

    public AccountTerminal(AccountService accountService, PrintWriter printer) {
        this.accountService = accountService;
        this.printer = printer;
    }

    /**
     * Returns and prints out user accounts from database or null if no any. if null prints out info message
     */
    public List<Account> getAndPrintAllAccounts(Long userId) throws SQLException {
        List<Account> userAccounts =  accountService.findAllByUserId(userId);
        if (!userAccounts.isEmpty()) {
            int i = 0;
            for (Account account: userAccounts) {
                printer.printf("%d. %s", ++i, account);
                printer.println();
            }
        } else {
            printer.println("No user accounts found");
        }

        return userAccounts;
    }
}
