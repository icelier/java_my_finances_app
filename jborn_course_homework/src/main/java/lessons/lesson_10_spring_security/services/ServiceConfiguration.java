package lessons.lesson_10_spring_security.services;

import lessons.lesson_10_spring_security.dao.DaoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@ComponentScan
@Import(DaoConfiguration.class)
@Configuration
public class ServiceConfiguration {

}
