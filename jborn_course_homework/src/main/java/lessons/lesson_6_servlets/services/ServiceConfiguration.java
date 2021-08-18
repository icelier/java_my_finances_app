package lessons.lesson_6_servlets.services;

import lessons.lesson_6_servlets.dao.DaoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@ComponentScan
@Import(DaoConfiguration.class)
@Configuration
public class ServiceConfiguration {

}
