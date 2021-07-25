package lessons.lesson_4_add_layers_and_factories.dao;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataFactory {
    private static DataSource dataSource;

    public static DataSource getDataSource() throws Exception {
        if (dataSource == null) {
            HikariDataSource hikariDataSource = new HikariDataSource();
            hikariDataSource.setJdbcUrl(System.getProperty("jdbcUrl",
                    "jdbc:postgresql://localhost:5432/postgres?currentSchema=finances"));
            hikariDataSource.setUsername(System.getProperty("jdbcUsername", "postgres"));
            hikariDataSource.setPassword(System.getProperty("jdbcPassword","clai531_Tre"));

            dataSource = hikariDataSource;

            initDatabase();
        }

        return dataSource;
    }

    private static void initDatabase() throws Exception {
        try {
            DatabaseConnection connection = new JdbcConnection(getDataSource().getConnection());
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
            Liquibase liquibase = new Liquibase(
                    "liquibase-outputChangeLog.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibase.update(new Contexts());
        } catch (SQLException | LiquibaseException e) {
            e.printStackTrace();
            throw new SQLException("Failed to implement database update");
        }
    }

    private DataFactory() {}
}
