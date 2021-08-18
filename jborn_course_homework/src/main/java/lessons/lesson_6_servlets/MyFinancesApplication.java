package lessons.lesson_6_servlets;

import lessons.lesson_6_servlets.servlets.ServletConfiguration;
import lessons.lesson_6_servlets.terminal_views.MainViewTerminal;
import lessons.lesson_6_servlets.terminal_views.TerminalConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MyFinancesApplication {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ServletConfiguration.class);
//        ApplicationContext context = new AnnotationConfigApplicationContext(TerminalConfiguration.class);
//        MainViewTerminal app = context.getBean(MainViewTerminal.class);
//        app.processUser();
    }
}
