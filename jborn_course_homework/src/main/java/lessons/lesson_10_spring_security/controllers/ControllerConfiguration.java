package lessons.lesson_10_spring_security.controllers;

import lessons.lesson_10_spring_security.services.ServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import(ServiceConfiguration.class)
public class ControllerConfiguration {

}
