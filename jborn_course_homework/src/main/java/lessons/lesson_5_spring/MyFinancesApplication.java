package lessons.lesson_5_spring;

import lessons.lesson_5_spring.terminal_views.TerminalConfiguration;
import lessons.lesson_5_spring.terminal_views.MainViewTerminal;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MyFinancesApplication {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(TerminalConfiguration.class);
        MainViewTerminal app = context.getBean(MainViewTerminal.class);
        app.processUser();
    }
}
