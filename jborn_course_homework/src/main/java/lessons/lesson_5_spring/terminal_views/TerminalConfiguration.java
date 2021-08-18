package lessons.lesson_5_spring.terminal_views;

import lessons.lesson_5_spring.services.ServiceConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.PrintWriter;
import java.util.Scanner;

@ComponentScan
@Import(ServiceConfiguration.class)
@Configuration
public class TerminalConfiguration {
    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean
    public PrintWriter printer() {
        return new PrintWriter(System.out, true);
    }
}
