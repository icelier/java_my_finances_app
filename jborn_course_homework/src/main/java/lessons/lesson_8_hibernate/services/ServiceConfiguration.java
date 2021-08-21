package lessons.lesson_8_hibernate.services;

import lessons.lesson_8_hibernate.dao.JpaConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan
@Import(JpaConfiguration.class)
@Configuration
public class ServiceConfiguration {

}
