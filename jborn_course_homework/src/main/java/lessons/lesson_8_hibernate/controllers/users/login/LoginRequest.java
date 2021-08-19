package lessons.lesson_8_hibernate.controllers.users.login;

import lombok.Data;

@Data
public class LoginRequest {
    private String userName;
    private String password;
    private String email;
}
