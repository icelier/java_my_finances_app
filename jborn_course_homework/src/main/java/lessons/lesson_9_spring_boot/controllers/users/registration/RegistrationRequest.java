package lessons.lesson_9_spring_boot.controllers.users.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationRequest {
    private final String userName;
    private final String password;
    private final String email;
}
