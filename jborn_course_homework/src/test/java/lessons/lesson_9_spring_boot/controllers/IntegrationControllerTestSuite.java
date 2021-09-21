package lessons.lesson_9_spring_boot.controllers;

import lessons.lesson_9_spring_boot.controllers.finances.accounts.IntegrationAccountControllerTest;
import lessons.lesson_9_spring_boot.controllers.finances.transactions.IntegrationTransactionControllerTest;
import lessons.lesson_9_spring_boot.controllers.users.login.IntegrationLoginControllerTest;
import lessons.lesson_9_spring_boot.controllers.users.registration.IntegrationRegistrationControllerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {IntegrationRegistrationControllerTest.class, IntegrationLoginControllerTest.class,
        IntegrationTransactionControllerTest.class, IntegrationAccountControllerTest.class})
public class IntegrationControllerTestSuite {
}
