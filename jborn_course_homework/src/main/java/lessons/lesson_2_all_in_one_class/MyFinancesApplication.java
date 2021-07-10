package lessons.lesson_2_all_in_one_class;

import lessons.entities.users.UserProjection;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Scanner;

public class MyFinancesApplication {
    private static final Scanner scanner = new Scanner(System.in);
    private static final PrintWriter printer = new PrintWriter(System.out);
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private static Connection connection = null;
    private static Statement statement = null;

    public static void main(String[] args) {
        processUser();
    }

    public static void processUser() {
        UserProjection currentUser = null;
        try {
            createConnectionToDB();
            createUserTable();

            greetUser();
            currentUser = registerOrLogin();

            showOptions();
            helpUser(currentUser);

            farewellUser();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    printer.println("Error closing database");
                    e.printStackTrace();
                }

            }
        }
    }

    private static void createConnectionToDB() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/postgres?currentSchema=finances",
                "postgres",
                "clai531_Tre"
        );
    }

    // used if no relations provided by other means
    private static void createUserTable() {
        try {
            statement = connection.createStatement();
            statement.executeQuery(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id serial primary key, " +
                            "username varchar(30) not null, " +
                            "password varchar(30) not null" +
                            ");");
        } catch (SQLException throwables) {
            printer.println("Failed to create statement");
            throwables.printStackTrace();
        }
    }

    private static void greetUser() {
        printer.println("Welcome, User!");
        printer.println("Please register or login if already registered");
    }

    private static UserProjection registerOrLogin() {
        printer.println("Do you have registered account at our Service?");
        printer.println("Type in Y for yes, N for no");
        UserProjection user = null;
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
                printer.println("type in Y for yes, N for no or \"exit\" to quit");
            }
        }

        return user;
    }

    private static UserProjection registerUser() {
        String name = getUsername();
        String password = getPassword();

        return saveUser(name, password);
    }

    private static UserProjection loginUser() {
        UserProjection user = null;
        String name = null;
        String password = null;

        while (user == null) {
            name = getUsername();
            password = getPassword();
            user = findUserInDB(name, password);
            if (user != null) {
                return user;
            }
            printer.println("Sorry, no such user in the database\n" +
                    "Please try again");
        }

        return null;
    }

    private static void showOptions() {
        printer.println("Please choose one of the options below:");
        printer.println("1. Show my accounts");
        printer.println("2. Show my transactions");
        printer.println("3. Show today transactions");
        printer.println("Enter \"exit\" to leave");
    }

    private static void helpUser(UserProjection user) {
        int commandNumber;
        while (scanner.hasNext()) {
            if (scanner.hasNextInt()) {
                commandNumber = scanner.nextInt();
                processChoice(commandNumber, user);
                printer.println("Do you want to try something else?");
                showOptions();
            } else if (scanner.nextLine().equalsIgnoreCase("exit")) {
                break;
            }
        }
    }

    private static boolean wantToQuit() {
        if (scanner.nextLine().equalsIgnoreCase("exit")) {
            return true;
        }

        return false;
    }

    private static void processChoice(int choice, UserProjection user) {
        switch (choice) {
            case 1:
                showAllAccounts(user);
                break;
            case 2:
                showAllTransactions(user);
                break;
            case 3:
                showTodayTransactions(user);
                break;
            default:
                printer.println("No such option in the list");
                break;
        }
    }

    private static String getUsername() {
        String question = "Please enter username";
        printer.println(question);
        String name = null;
        while (scanner.hasNext()) {
            if (scanner.hasNextLine()) {
                name = scanner.nextLine();
                if (!name.isEmpty()) {
                    break;
                } else {
                    printer.println(question);
                }
            }
        }

        return name;
    }

    private static String getPassword() {
        String question = "Please enter password";
        printer.println(question);
        String password = null;
        while (scanner.hasNext()) {
            if (scanner.hasNextLine()) {
                password = scanner.nextLine();
                if (!password.isEmpty()) {
                    break;
                } else {
                    printer.println(question);
                }
            }
        }

        return password;
    }

    private static UserProjection findUserInDB(String name, String password) {
        String getUserFromDB = "SELECT * FROM users WHERE username=?";
        try {
            PreparedStatement ps = connection.prepareStatement(getUserFromDB);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            UserProjection user = null;
            if (rs.next()) {
                if (!encoder.matches(password, rs.getString("password"))) {
                    return null;
                }
                printer.println("User found from db: " + rs.getString(2));

                user = new UserProjection(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            }

            return user;
        } catch (SQLException e) {
            printer.println("Error when getting user from the database");
            e.printStackTrace();
        }

        return null;
    }

    private static void showAllAccounts(UserProjection user) {
        String getAllAccounts = "SELECT name, total FROM accounts WHERE user_id=?";
        try {

            PreparedStatement ps = connection.prepareStatement(getAllAccounts);
            ps.setLong(1, user.getId());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                printer.println("Account: " + rs.getString("name") +
                        " total: " + rs.getBigDecimal("total"));
            }
        } catch (SQLException throwables) {
            printer.println("Failed to get user accounts from database");
            throwables.printStackTrace();
        }
    }

    private static void showAllTransactions(UserProjection user) {
        String getAllTransactions = "SELECT transactions.* FROM transactions INNER JOIN accounts ON " +
                "transactions.account_id=accounts.id WHERE accounts.user_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(getAllTransactions);
            ps.setLong(1, user.getId());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                printer.println("Transaction: " +
                        rs.getString("type") +
                        " for sum: " + rs.getBigDecimal("transfer") +
                        " carried on: " + rs.getTimestamp("ts") +
                        " for category: " + rs.getString("category_id")
                );
            }
        } catch (SQLException e) {
            printer.println("Failed to get user transactions from database");
            e.printStackTrace();
        }
    }

    private static void showTodayTransactions(UserProjection user) {
        String getAllTransactions = "SELECT transactions.* FROM transactions INNER JOIN accounts ON " +
                "transactions.account_id=accounts.id WHERE accounts.user_id=?" +
                "AND transactions.ts BETWEEN ? AND ?";
        try {
            PreparedStatement ps = connection.prepareStatement(getAllTransactions);
            ps.setLong(1,
                    user.getId());
            ps.setTimestamp(2,
                    Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT)));
            ps.setTimestamp(3,
                    Timestamp.valueOf(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT)));

            ResultSet rs = ps.executeQuery();
            printer.println("Today " + LocalDate.now() + " transactions:");
            while (rs.next()) {
                printer.println("Transaction: " +
                        rs.getString("type") +
                        " for sum: " + rs.getBigDecimal("transfer") +
                        " carried on: " + rs.getTimestamp("ts") +
                        " for category: " + rs.getString("category_id")
                );
            }
        } catch (SQLException e) {
            printer.println("Failed to get user today transactions from database");
            e.printStackTrace();
        }
    }

    // fullname, email, age omitted for simplicity
    private static UserProjection saveUser(String name, String password) {
        String insertIntoUsers = "INSERT INTO users (username, password, fullname, email) " +
                "VALUES (?, ?, ?, DEFAULT);";
        try {
            PreparedStatement ps = connection.prepareStatement(insertIntoUsers);
            ps.setString(1, name);
            ps.setString(2, encoder.encode(password));
            ps.setString(3, name);
            ps.executeQuery();

            return findUserInDB(name, password);
        } catch (SQLException e) {
            printer.println("Error when saving new user to the database");
            e.printStackTrace();
        }

        return null;
    }

    private static void farewellUser() {
        printer.println("Have a good day!");
        printer.println("Come to us again!");
    }
}
