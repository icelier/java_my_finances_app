package lessons.lesson_4.singleton_factories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lessons.lesson_4.dao.finances.AccountDao;
import lessons.lesson_4.dao.finances.AccountTypeDao;
import lessons.lesson_4.dao.finances.CategoryDao;
import lessons.lesson_4.dao.finances.TransactionDao;
import lessons.lesson_4.dao.users.RoleDao;
import lessons.lesson_4.dao.users.UserDao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DaoFactory {
    // data source
    private static DataSource dataSource;
    private static HikariConfig config = new HikariConfig();

    public static DataSource getDataSource() throws SQLException {
        if (dataSource == null) {

            config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres?currentSchema=finances");
            config.setUsername("postgres");
            config.setPassword("clai531_Tre");
            config.setMaximumPoolSize(96);

            dataSource = new HikariDataSource(config);
        }

        return dataSource;
    }

    // users
    private static UserDao userDao;

    public static UserDao getUserDao() throws SQLException {
        if (userDao == null) {
            userDao = new UserDao(getDataSource());
        }
        return userDao;
    }

    private static RoleDao roleDao;

    public static RoleDao getRoleDao() throws SQLException {
        if (roleDao == null) {
            roleDao = new RoleDao(getDataSource());
        }
        return roleDao;
    }

    // finances
    private static AccountDao accountDao;

    public static AccountDao getAccountDao() throws SQLException {
        if (accountDao == null) {
            accountDao = new AccountDao(getDataSource());
        }
        return accountDao;
    }

    private static AccountTypeDao accountTypeDao;

    public static AccountTypeDao getAccountTypeDao() throws SQLException {
        if (accountTypeDao == null) {
            accountTypeDao = new AccountTypeDao(getDataSource());
        }
        return accountTypeDao;
    }

    private static CategoryDao categoryDao;

    public static CategoryDao getCategoryDao() throws SQLException {
        if (categoryDao == null) {
            categoryDao = new CategoryDao(getDataSource());
        }
        return categoryDao;
    }

    private static TransactionDao transactionDao;

    public static TransactionDao getTransactionDao() throws SQLException {
        if (transactionDao == null) {
            transactionDao = new TransactionDao(getDataSource());
        }
        return transactionDao;
    }

}
