package lessons.lesson_4_add_layers_and_factories;

import lessons.lesson_4_add_layers_and_factories.entities.users.UserProjection;
import lessons.lesson_4_add_layers_and_factories.singleton_factories.TerminalFactory;
import lessons.lesson_4_add_layers_and_factories.singleton_factories.UtilsFactory;
import lessons.lesson_4_add_layers_and_factories.terminal_views.AccountTerminal;
import lessons.lesson_4_add_layers_and_factories.terminal_views.TransactionTerminal;
import lessons.lesson_4_add_layers_and_factories.terminal_views.UserTerminal;
import lessons.lesson_4_add_layers_and_factories.terminal_views.UtilTerminal;

import java.io.PrintWriter;

public class MyFinancesApplication {
    private PrintWriter printer;
    private AccountTerminal accountTerminal;
    private TransactionTerminal transactionTerminal;
    private UserTerminal userTerminal;
    private UtilTerminal utilTerminal;

    private MyFinancesApplication() throws Exception {
        printer = UtilsFactory.getPrinter();
        accountTerminal = TerminalFactory.getAccountTerminal();
        transactionTerminal = TerminalFactory.getTransactionTerminal();
        utilTerminal = TerminalFactory.getUtilTerminal();
        userTerminal = TerminalFactory.getUserTerminal();
    }

    public static void main(String[] args) throws Exception {
        MyFinancesApplication app = new MyFinancesApplication();
        app.processUser();
    }

    public void processUser() throws Exception {
        UserProjection currentUser;

        greetUser();
        currentUser = new UserProjection(userTerminal.registerOrLogin());

        showOptions();
        helpUser(currentUser);

        farewellUser();
        if (printer != null) {
            printer.close();
        }
        if (UtilsFactory.getScanner() != null) {
            UtilsFactory.getScanner().close();
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

    private void helpUser(UserProjection user) throws Exception {
        while (true) {
            int choice = utilTerminal.getUserChoice();
            if (choice == 0) {
                return;
            }
            processChoice(choice, user);

            printer.println("Do you want to try something else?");
            showOptions();
        }
    }



    private void processChoice(int choice, UserProjection user) throws Exception {
        switch (choice) {
            case 0:
                break;
            case 1:
                accountTerminal.getAndPrintAllAccounts(user.getId());
                break;
            case 2:
                transactionTerminal.printAllTransactions(user.getId());
                break;
            case 3:
                transactionTerminal.printTodayTransactions(user.getId());
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
}
