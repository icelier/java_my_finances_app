package lessons.lesson_7_controllers.controllers;

import lessons.lesson_7_controllers.services.ServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import(ServiceConfiguration.class)
public class ControllerConfiguration {

}
