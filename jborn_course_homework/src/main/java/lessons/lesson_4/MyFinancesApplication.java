package lessons.lesson_4;

import lessons.lesson_4.entities.finances.Account;
import lessons.lesson_4.entities.finances.Transaction;
import lessons.lesson_4.entities.users.UserEntity;
import lessons.lesson_4.entities.users.UserProjection;
import lessons.lesson_4.services.finances.AccountServiceImpl;
import lessons.lesson_4.services.finances.TransactionServiceImpl;
import lessons.lesson_4.services.users.UserServiceImpl;
import lessons.lesson_4.singleton_factories.ServiceFactory;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class MyFinancesApplication {
    private final static Scanner scanner = new Scanner(System.in);
    private static UserServiceImpl userService;
    private static AccountServiceImpl accountService;
    private static TransactionServiceImpl transactionService;

    static {
        try {
            userService = ServiceFactory.getUserService();
            accountService = ServiceFactory.getAccountService();
            transactionService = ServiceFactory.getTransactionService();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        processUser();
    }

    public static void processUser() throws SQLException {
        UserProjection currentUser = null;

        greetUser();
        currentUser = new UserProjection(registerOrLogin());

        showOptions();
        helpUser(currentUser);

        farewellUser();
    }

    private static void greetUser() {
        System.out.println("Welcome, User!");
        System.out.println("Please register or login if already registered");
    }

    private static UserEntity registerOrLogin() throws SQLException {
        System.out.println("Do you have registered account at our Service?");
        System.out.println("Type in Y for yes, N for no");
        UserEntity user = null;
        String answer = null;
        while (scanner.hasNext()) {
            answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("y")) {
                user = loginUser();
                break;
            } else if (answer.equalsIgnoreCase("n")) {
                user = registerUser();
                break;
            } else if (answer.equalsIgnoreCase("exit")) {
                break;
            } else {
                System.out.println("type in Y for yes, N for no or \"exit\" to quit");
            }
        }

        return user;
    }

    private static UserEntity registerUser() throws SQLException {
        String name = getUsername();
        String password = getPassword();
        UserEntity newUser = new UserEntity(name, password);

        return userService.insert(newUser);
    }

    private static UserEntity loginUser() throws SQLException {
        UserEntity user = null;
        String name = null;
        String password = null;

        while (user == null) {
            name = getUsername();
            password = getPassword();
            user = userService.findByUserNameAndPassword(name, password);
            if (user != null) {
                return user;
            }
            System.out.println("Sorry, no such user in the database\n" +
                    "Please try again");
        }

        return null;
    }

    private static void showOptions() {
        System.out.println("Please choose one of the options below:");
        System.out.println("1. Show my accounts");
        System.out.println("2. Show my transactions");
        System.out.println("3. Show today transactions");
        System.out.println("Enter \"exit\" to leave");
    }

    private static void helpUser(UserProjection user) throws SQLException {
        int commandNumber;
        while (scanner.hasNext()) {
            if (scanner.hasNextInt()) {
                commandNumber = scanner.nextInt();
                processChoice(commandNumber, user);
                System.out.println("Do you want to try something else?");
                showOptions();
            } else if (scanner.nextLine().equalsIgnoreCase("exit")) {
                break;
            }
        }
    }

    private static void processChoice(int choice, UserProjection user) throws SQLException {
        switch (choice) {
            case 1:
                showAllAccounts(user.getId());
                break;
            case 2:
                showAllTransactions(user.getId());
                break;
            case 3:
                showTodayTransactions(user.getId());
                break;
            default:
                System.out.println("No such option in the list");
                break;
        }
    }

    private static String getUsername() {
        String question = "Please enter username";
        System.out.println(question);
        String name = null;
        while (scanner.hasNext()) {
            if (scanner.hasNextLine()) {
                name = scanner.nextLine();
                if (!name.isEmpty()) {
                    break;
                } else {
                    System.out.println(question);
                }
            }
        }

        return name;
    }

    private static String getPassword() {
        String question = "Please enter password";
        System.out.println(question);
        String password = null;
        while (scanner.hasNext()) {
            if (scanner.hasNextLine()) {
                password = scanner.nextLine();
                if (!password.isEmpty()) {
                    break;
                } else {
                    System.out.println(question);
                }
            }
        }

        return password;
    }


    private static void showAllAccounts(Long userId) throws SQLException {
        List<Account> userAccounts = accountService.findAllByUserId(userId);
        if (userAccounts.isEmpty()) {
            System.out.println("No user accounts registered");
        } else {
            for (Account account:
                 userAccounts) {
                System.out.println("Account: " + account.getName() +
                        " total: " + account.getSum());
            }
        }
    }

    private static void showAllTransactions(Long userId) throws SQLException {
        List<Transaction> userTransactions = transactionService.findAllByUserId(userId);
        if (userTransactions.isEmpty()) {
            System.out.println("No user transactions registered");
        } else {
            for (Transaction transaction:
                    userTransactions) {
                System.out.println(transaction);
            }
        }
    }

    private static void showTodayTransactions(Long userId) throws SQLException {
        List<Transaction> userTodayTransactions = transactionService.findAllByUserIdToday(userId);
        if (userTodayTransactions.isEmpty()) {
            System.out.println("No user transactions for today");
        } else {
            System.out.println("Your today transactions:");
            for (Transaction transaction:
                    userTodayTransactions) {
                System.out.println(transaction);
            }
        }
    }

    private static void farewellUser() {
        System.out.println("Have a good day!");
        System.out.println("Come to us again!");
    }
}
