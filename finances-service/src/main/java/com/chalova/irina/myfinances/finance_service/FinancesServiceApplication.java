package com.chalova.irina.myfinances.finance_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableEurekaClient
@SpringBootApplication
public class FinancesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancesServiceApplication.class, args);
    }
}
