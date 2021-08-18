package lessons.lesson_7_controllers.controllers.users.login;

import lessons.lesson_7_controllers.controllers.Controller;
import lessons.lesson_7_controllers.entities.users.UserEntity;
import lessons.lesson_7_controllers.services.users.UserService;
import java.sql.SQLException;

@org.springframework.stereotype.Controller("login")
public class LoginController implements Controller<LoginRequest, LoginResponse> {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public LoginResponse execute(LoginRequest request) throws Exception {
        Long userId = null;
        UserEntity user = userService.findByUserName(request.getUserName());
        if (user == null) {
            return LoginResponse.FAILURE;
        } else {
            return LoginResponse.SUCCESS;
        }
//        catch (SQLException e) {
//            e.printStackTrace();
//            return LoginResponse.ERROR;
//        }
    }

    @Override
    public Class<LoginRequest> getRequestClass() {
        return LoginRequest.class;
    }
}
