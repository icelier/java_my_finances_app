package lessons.lesson_8_hibernate.controllers.users.login;

import lessons.lesson_8_hibernate.controllers.Controller;
import lessons.lesson_8_hibernate.controllers.users.login.LoginRequest;
import lessons.lesson_8_hibernate.controllers.users.login.LoginResponse;
import lessons.lesson_8_hibernate.entities.users.UserEntity;
import lessons.lesson_8_hibernate.services.users.UserService;

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
