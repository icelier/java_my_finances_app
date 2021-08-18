package lessons.lesson_7_controllers.dao;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@ComponentScan
@Configuration
public class DaoConfiguration {

    @Bean
    public Liquibase liquibase(DataSource dataSource) throws Exception {
        DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
        Liquibase liquibase = new Liquibase(
                "liquibase-outputChangeLog.xml",
                new ClassLoaderResourceAccessor(),
                database
        );
        liquibase.update(new Contexts());

        return liquibase;
    }

    @Bean
    public DataSource dataSource(Environment env) throws Exception {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(env.getProperty("jdbcUrl",
                "jdbc:postgresql://localhost:5432/postgres?currentSchema=finances"));
        hikariDataSource.setUsername(env.getProperty("jdbcUsername", "postgres"));
        hikariDataSource.setPassword(env.getProperty("jdbcPassword","clai531_Tre"));
        hikariDataSource.setDriverClassName("org.postgresql.Driver");
        liquibase(hikariDataSource);

        return hikariDataSource;
    }

}
