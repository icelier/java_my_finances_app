package lessons.lesson_8_hibernate.controllers.users.registration;

import lessons.lesson_8_hibernate.controllers.Controller;
import lessons.lesson_8_hibernate.controllers.users.registration.RegistrationRequest;
import lessons.lesson_8_hibernate.controllers.users.registration.RegistrationResponse;
import lessons.lesson_8_hibernate.entities.users.UserEntity;
import lessons.lesson_8_hibernate.services.users.UserService;

import java.sql.SQLException;

@org.springframework.stereotype.Controller("registration")
public class RegistrationController implements Controller<RegistrationRequest, RegistrationResponse> {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public RegistrationResponse execute(RegistrationRequest request) throws SQLException {
        Long userId = null;
//        try {
            UserEntity user = userService.findByUserName(request.getUserName());
            if (user != null) {
                return RegistrationResponse.FAILURE;
            } else {
                return RegistrationResponse.SUCCESS;
            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return RegistrationResponse.ERROR;
//        }
    }

    @Override
    public Class<RegistrationRequest> getRequestClass() {
        return RegistrationRequest.class;
    }
}
