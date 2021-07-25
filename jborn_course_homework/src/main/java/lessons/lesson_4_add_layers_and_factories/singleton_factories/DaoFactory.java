package lessons.lesson_4_add_layers_and_factories.singleton_factories;

import lessons.lesson_4_add_layers_and_factories.dao.DataFactory;
import lessons.lesson_4_add_layers_and_factories.dao.finances.AccountDao;
import lessons.lesson_4_add_layers_and_factories.dao.finances.AccountTypeDao;
import lessons.lesson_4_add_layers_and_factories.dao.finances.CategoryDao;
import lessons.lesson_4_add_layers_and_factories.dao.finances.TransactionDao;
import lessons.lesson_4_add_layers_and_factories.dao.users.RoleDao;
import lessons.lesson_4_add_layers_and_factories.dao.users.UserDao;

import javax.sql.DataSource;

public class DaoFactory {
    // data source
    private static DataSource dataSource;
    public static DataSource getDataSource() throws Exception {
        if (dataSource == null) {
            dataSource = DataFactory.getDataSource();
        }
        return dataSource;
    }

    // users
    private static UserDao userDao;

    public static UserDao getUserDao() throws Exception {
        if (userDao == null) {
            userDao = new UserDao(getDataSource());
        }
        return userDao;
    }

    private static RoleDao roleDao;

    public static RoleDao getRoleDao() throws Exception {
        if (roleDao == null) {
            roleDao = new RoleDao(getDataSource());
        }
        return roleDao;
    }

    // finances
    private static AccountDao accountDao;

    public static AccountDao getAccountDao() throws Exception {
        if (accountDao == null) {
            accountDao = new AccountDao(getDataSource());
        }
        return accountDao;
    }

    private static AccountTypeDao accountTypeDao;

    public static AccountTypeDao getAccountTypeDao() throws Exception {
        if (accountTypeDao == null) {
            accountTypeDao = new AccountTypeDao(getDataSource());
        }
        return accountTypeDao;
    }

    private static CategoryDao categoryDao;

    public static CategoryDao getCategoryDao() throws Exception {
        if (categoryDao == null) {
            categoryDao = new CategoryDao(getDataSource());
        }
        return categoryDao;
    }

    private static TransactionDao transactionDao;

    public static TransactionDao getTransactionDao() throws Exception {
        if (transactionDao == null) {
            transactionDao = new TransactionDao(getDataSource());
        }
        return transactionDao;
    }

}
