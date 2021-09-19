package lessons.lesson_9_spring_boot.services;

import lessons.lesson_9_spring_boot.services.finances.AccountServiceTest;
import lessons.lesson_9_spring_boot.services.finances.TransactionServiceTest;
import lessons.lesson_9_spring_boot.services.users.UserServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {AccountServiceTest.class, TransactionServiceTest.class,
        UserServiceTest.class})
public class ServiceTestSuite {
}
