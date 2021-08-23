package lessons.lesson_9_spring_boot.controllers;

import lessons.lesson_9_spring_boot.services.ServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import(ServiceConfiguration.class)
public class ControllerConfiguration {

}
