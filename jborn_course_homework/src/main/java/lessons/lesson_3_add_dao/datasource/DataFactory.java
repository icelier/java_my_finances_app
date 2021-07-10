package lessons.lesson_3_add_dao.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataFactory {
    private static DataSource dataSource;
    private static HikariConfig config = new HikariConfig();

    static {
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres?currentSchema=finances");
        config.setUsername("postgres");
        config.setPassword("clai531_Tre");
        config.setMaximumPoolSize(96);

        dataSource = new HikariDataSource(config);
    }

    private DataFactory() {}


    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
