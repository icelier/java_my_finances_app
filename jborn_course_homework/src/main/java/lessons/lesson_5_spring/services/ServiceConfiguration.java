package lessons.lesson_5_spring.services;

import lessons.lesson_5_spring.dao.DaoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@ComponentScan
@Import(DaoConfiguration.class)
@Configuration
public class ServiceConfiguration {

}
