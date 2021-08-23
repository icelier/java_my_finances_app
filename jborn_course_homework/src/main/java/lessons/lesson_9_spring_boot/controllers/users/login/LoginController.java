package lessons.lesson_9_spring_boot.controllers.users.login;

import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_match_exceptions.PasswordNotMatchException;
import lessons.lesson_9_spring_boot.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/my-finances/users")
public class LoginController {
    private final UserService userService;
    private final UserToLoginResponseConverter converterToLoginResponse;

    @RequestMapping(path = "/login",
            method = RequestMethod.POST,
            consumes = { MediaType.APPLICATION_JSON_VALUE},
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        UserEntity user;
        try {
            user = userService.checkPasswordByUserName(request.getUserName(),
                    request.getPassword());
            return ok(converterToLoginResponse.convert(user));
        } catch (UserNotFoundException | PasswordNotMatchException e) {
           return notFound().build();
        }
    }

}
