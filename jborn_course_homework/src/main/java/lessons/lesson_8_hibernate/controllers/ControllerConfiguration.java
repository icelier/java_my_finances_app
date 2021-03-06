package lessons.lesson_8_hibernate.controllers;

import lessons.lesson_8_hibernate.services.ServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan
@Import(ServiceConfiguration.class)
public class ControllerConfiguration {

}
