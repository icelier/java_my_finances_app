package lessons.lesson_9_spring_boot.controllers.users.login;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    private final String userName;
    private final String password;
}
