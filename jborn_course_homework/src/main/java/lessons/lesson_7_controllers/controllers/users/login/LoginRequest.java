package lessons.lesson_7_controllers.controllers.users.login;

import lombok.Data;

@Data
public class LoginRequest {
    private String userName;
    private String password;
    private String email;
}
