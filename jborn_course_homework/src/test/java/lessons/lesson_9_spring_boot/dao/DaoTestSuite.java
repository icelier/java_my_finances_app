package lessons.lesson_9_spring_boot.dao;

import lessons.lesson_9_spring_boot.dao.finances.AccountDaoTest;
import lessons.lesson_9_spring_boot.dao.finances.AccountTypeDaoTest;
import lessons.lesson_9_spring_boot.dao.finances.CategoryDaoTest;
import lessons.lesson_9_spring_boot.dao.finances.TransactionDaoTest;
import lessons.lesson_9_spring_boot.dao.users.RoleDaoTest;
import lessons.lesson_9_spring_boot.dao.users.UserDaoTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {AccountDaoTest.class, AccountTypeDaoTest.class,
        CategoryDaoTest.class, TransactionDaoTest.class,
        RoleDaoTest.class, UserDaoTest.class})
public class DaoTestSuite {
}
