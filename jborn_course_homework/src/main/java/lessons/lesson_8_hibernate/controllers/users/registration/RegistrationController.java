package lessons.lesson_8_hibernate.controllers.users.registration;

import lessons.lesson_8_hibernate.controllers.Controller;
import lessons.lesson_8_hibernate.controllers.users.registration.RegistrationRequest;
import lessons.lesson_8_hibernate.controllers.users.registration.RegistrationResponse;
import lessons.lesson_8_hibernate.entities.users.UserEntity;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_8_hibernate.services.users.UserService;

import java.sql.SQLException;

// for http://localhost:8080/my-finances/registration POST
@org.springframework.stereotype.Controller("registration")
public class RegistrationController implements Controller<RegistrationRequest, RegistrationResponse> {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public RegistrationResponse execute(RegistrationRequest request) throws UserNotFoundException, OperationFailedException, UserAlreadyExistsException {
        Long userId = null;
//        try {
            UserEntity user = userService.findByUserName(request.getUserName());
            if (user != null) {
                return RegistrationResponse.FAILURE;
            } else {
                user = new UserEntity(request.getUserName(), request.getPassword(), request.getEmail());
                user = userService.insert(user);
                System.out.println("Inserted user = " + user);
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
