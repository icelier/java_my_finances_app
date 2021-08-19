package lessons.lesson_8_hibernate.controllers.users.registration;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String userName;
    private String password;
    private String email;
}
