package lessons.lesson_10_spring_security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"lessons.lesson_10_spring_security"})
public class MyApplication {
    public static final String BASE_URL = "/my-finances";

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

}
