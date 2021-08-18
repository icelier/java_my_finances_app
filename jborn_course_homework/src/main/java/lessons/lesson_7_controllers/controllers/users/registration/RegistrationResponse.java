package lessons.lesson_7_controllers.controllers.users.registration;

import lessons.lesson_7_controllers.controllers.users.login.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {
    private boolean registrationSucceed;

    static final RegistrationResponse FAILURE;
    static final RegistrationResponse SUCCESS;
    static final RegistrationResponse ERROR;

    static {
        FAILURE = new RegistrationResponse(false);
        SUCCESS = new RegistrationResponse(true);
        ERROR = new RegistrationResponse();
    }
}
