package lessons.lesson_7_controllers.controllers.users.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private boolean loginSucceed;

    static final LoginResponse FAILURE;
    static final LoginResponse SUCCESS;
    static final LoginResponse ERROR;

    static {
        FAILURE = new LoginResponse(false);
        SUCCESS = new LoginResponse(true);
        ERROR = new LoginResponse();
    }

}
