package lessons.lesson_9_spring_boot.controllers;

import lessons.lesson_9_spring_boot.controllers.finances.accounts.AccountsControllerTest;
import lessons.lesson_9_spring_boot.controllers.finances.transactions.TransactionControllerTest;
import lessons.lesson_9_spring_boot.controllers.users.login.LoginControllerTest;
import lessons.lesson_9_spring_boot.controllers.users.registration.RegistrationControllerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {RegistrationControllerTest.class, LoginControllerTest.class,
        TransactionControllerTest.class, AccountsControllerTest.class})
public class ControllersTestSuite {
}
