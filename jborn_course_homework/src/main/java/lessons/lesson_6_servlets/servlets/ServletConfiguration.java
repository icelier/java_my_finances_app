package lessons.lesson_6_servlets.servlets;

import lessons.lesson_6_servlets.services.ServiceConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.PrintWriter;
import java.util.Scanner;

@ComponentScan
@Import(ServiceConfiguration.class)
@Configuration
public class ServletConfiguration {

}

