package lessons.lesson_5_spring.terminal_views;

import lessons.lesson_5_spring.entities.users.UserEntity;
import lessons.lesson_5_spring.entities.users.UserLoginProjection;
import lessons.lesson_5_spring.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_5_spring.exceptions.not_match_exceptions.PasswordNotMatchException;
import lessons.lesson_5_spring.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_5_spring.services.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class UserTerminal {
    public static final Pattern VALID_EMAIL_ADDRESS_PATTERN =
            Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");

    private final Logger logger = LoggerFactory.getLogger(UserTerminal.class);
    private final UserService userService;
    private final Scanner scanner;
    private final PrintWriter printer;

    public UserTerminal(UserService userService, PrintWriter printer, Scanner scanner) {
        this.userService = userService;
        this.printer = printer;
        this.scanner = scanner;
    }

    private boolean needRegistration() {
        printer.println("Do you have registered account at our Service?");
        printer.println("Type in Y for yes, N for no");
        String answer;
        while (scanner.hasNext()) {
            answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("y")) {
                return false;
            } else if (answer.equalsIgnoreCase("n")) {
                return true;
            } else {
                printer.println("Type in Y for yes, N for no");
            }
        }

        return false;
    }

    /**
     * Inserts new user entity into database or gets current user logged in by username and password.
     * If user found in the database by username and password during registration, redirected to logging in process
     * @return userLoginProjection if registered or logged in successfully
     * @throws SQLException if database access error occurred, if underlying query failed
     * @throws OperationFailedException  if user insertion into database failed
     * @throws PasswordNotMatchException if failed to enter password for username found in the database for 3 attempts
     */
    UserLoginProjection registerOrLogin() throws PasswordNotMatchException, SQLException, OperationFailedException {
        UserLoginProjection user = null;

        if (needRegistration()) {
            try {
                user = registerUser();
            } catch (UserAlreadyExistsException e) {
                printer.println("Sorry, such user already exists. Try to login");
                return loginUser();
            }
        } else {
            user = loginUser();
        }

        return user;
    }

    /**
     * Registers new user by given username and password, checking if user already exists in the database by given username
     * @return registered userLoginProjection of the new user
     * @throws UserAlreadyExistsException if user found in the database by given username
     * @throws SQLException if database access error occurred, if underlying query failed
     * @throws OperationFailedException if user insertion into database failed
     */
    private UserLoginProjection registerUser() throws UserAlreadyExistsException, SQLException, OperationFailedException {
        String name = getUsername();
        String password = getPassword();
        String email = getEmail();
        UserEntity newUser = new UserEntity(name, password, email);
        newUser = userService.insert(newUser);
        printer.println("You are registered!");

        return new UserLoginProjection(newUser);
    }

    /**
     * Returns userLoginProjection for successfully logged in user
     * @return logged in userLoginProjection
     * @throws SQLException if database access error occurred, if underlying query failed
     * @throws PasswordNotMatchException if failed to enter password for username found in the database for 3 attempts
     */
    private UserLoginProjection loginUser() throws SQLException, PasswordNotMatchException {

        UserLoginProjection loggedInUser = getLoggedInUser();
        if (loggedInUser != null) {
            printer.println("You successfully logged in!");
        }

        return loggedInUser;
    }

    /**
     * Returns logged in user projection from user entity found in the database by username and password provided.
     * Gives 3 attempts to enter password for each username. After 3 attempts, throws PasswordNotMatchException
     * @return UserLoginProjection of the found user entity
     * @throws SQLException if database access error occurred, if underlying query failed
     * @throws PasswordNotMatchException if 3 attempts to enter password for specified username exausted
     */
    private UserLoginProjection getLoggedInUser() throws SQLException, PasswordNotMatchException {
        UserEntity user = null;
        String userName;
        String password;
        int counter = 3;
        while (user == null) {
            printer.println("Please enter your name and password for log in.\n" +
                    "You have 3 attempts to enter password for your account");

            userName = getUsername();
            user = userService.findByUserName(userName);
            if (user != null) {
                while (--counter >= 0) {
                    password = getPassword();
                    boolean passwordCorrect = userService.checkPasswordCorrect(password, user);
                    if (!passwordCorrect) {
                        printer.println("Wrong password. Attempts left: " + counter);
                    } else {
                        return new UserLoginProjection(user);
                    }
                }
                throw new PasswordNotMatchException("You have exausted all attempts to enter your account");
            }

            printer.println("Username not found");
        }

        return null;
    }

    private String getUsername() {
        String question = "Please enter username";
        printer.println(question);
        String name = null;
        while (scanner.hasNext()) {
            if (scanner.hasNextLine()) {
                name = scanner.nextLine();
                if (!name.trim().isEmpty()) {
                    break;
                }
                printer.println(question);
            }
        }

        return name;
    }

    private String getPassword() {
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

    private String getEmail() {
        String question = "Please enter your email";
        printer.println(question);
        String email = null;
        while (scanner.hasNext()) {
            if (scanner.hasNextLine()) {
                email = scanner.nextLine();
                if (!matchEmail(email)) {
                    printer.println("Please enter valid email address");
                } else {
                    break;
                }
            }
        }

        return email;
    }

    private boolean matchEmail(String emailStr) {
        Matcher matcher= VALID_EMAIL_ADDRESS_PATTERN.matcher(emailStr);
        return matcher.matches();
    }
}
