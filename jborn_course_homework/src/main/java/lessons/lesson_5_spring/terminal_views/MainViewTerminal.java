package lessons.lesson_5_spring.terminal_views;

import lessons.lesson_5_spring.entities.finances.Account;
import lessons.lesson_5_spring.entities.finances.Transaction;
import lessons.lesson_5_spring.entities.users.UserLoginProjection;
import lessons.lesson_5_spring.exceptions.not_match_exceptions.AccountNotMatchException;
import lessons.lesson_5_spring.exceptions.not_match_exceptions.PasswordNotMatchException;
import lessons.lesson_5_spring.exceptions.operation_failed.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

@Controller
public class MainViewTerminal {
    private final Logger logger = LoggerFactory.getLogger(MainViewTerminal.class);

    private PrintWriter printer;
    private Scanner scanner;
    private AccountTerminal accountTerminal;
    private TransactionTerminal transactionTerminal;
    private UserTerminal userTerminal;
    private UtilTerminal utilTerminal;

    public MainViewTerminal(
            PrintWriter printer,
            Scanner scanner,
            AccountTerminal accountTerminal,
            TransactionTerminal transactionTerminal,
            UtilTerminal utilTerminal,
            UserTerminal userTerminal
    ) throws Exception {

        this.printer = printer;
        this.scanner = scanner;
        this.accountTerminal = accountTerminal;
        this.transactionTerminal = transactionTerminal;
        this.utilTerminal = utilTerminal;
        this.userTerminal = userTerminal;
    }

    public void processUser() {
        UserLoginProjection currentUser = null;

        greetUser();
        try {
            try {
                currentUser = userTerminal.registerOrLogin();
            } catch (PasswordNotMatchException e) {
                printer.println("Failed to log in");
            }

            if (currentUser == null) {
                farewellUser();
                shutDownTerminal();
                return;
            }
            printer.println("Below services now available for you");
            showOptions();
            helpUser(currentUser);
        } catch (SQLException | OperationFailedException e) {
            logger.error(e.getLocalizedMessage());
            printer.println("Sorry, operation failed due to internal error. Try again later");
        } finally {
            farewellUser();
            shutDownTerminal();
        }
    }

    private void greetUser() {
        printer.println("Welcome, User!");
        printer.println("Please register or login if already registered");
    }

    private void showOptions() {
        printer.println("Please choose one of the options below:");
        printer.println("1. Show my accounts");
        printer.println("2. Show my transactions");
        printer.println("3. Show today transactions");
        printer.println("4. Make transaction");
        printer.println("Enter 0 for exit");
    }

    private void helpUser(UserLoginProjection user) throws SQLException, OperationFailedException {
        while (true) {
            int choice = utilTerminal.getUserChoice();
            if (choice == 0) {
                return;
            }
            try {
                processChoice(choice, user);
            } catch (AccountNotMatchException e) {
                printer.println(e.getLocalizedMessage());
            }

            printer.println("Do you want to try something else?");
            showOptions();
        }
    }



    private void processChoice(int choice, UserLoginProjection user) throws SQLException, AccountNotMatchException, OperationFailedException {
        switch (choice) {
            case 0:
                break;
            case 1:
                accountTerminal.getAndPrintAllAccounts(user.getId());
                break;
            case 2:
                transactionTerminal.getAndPrintAllTransactions(user.getId());
                break;
            case 3:
                transactionTerminal.getAndPrintTodayTransactions(user.getId());
                break;
            case 4:
                transactionTerminal.makeTransaction(user);
                break;
            default:
                printer.println("No such option in the list");
                break;
        }
    }

    private void farewellUser() {
        printer.println("Have a good day!");
        printer.println("Come to us again!");
    }

    private void shutDownTerminal() {
        if (printer != null) {
            printer.close();
        }
        if (scanner != null) {
            scanner.close();
        }
    }
}
