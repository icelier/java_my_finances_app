package com.chalova.irina.myfinances.myfinances_client;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@EnableEurekaClient
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@Configuration
public class MyFinancesClient {

    public static void main(String[] args) {
        SpringApplication.run(MyFinancesClient.class, args);
    }

    @Autowired
    private DataSource dataSource;

    @Value("${changeLogFile}")
    private String changeLog;

    @Bean
    @DependsOn
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(changeLog);
        liquibase.setDataSource(dataSource);
        return liquibase;
    }
}
