package lessons.lesson_9_spring_boot.controllers.users.login;

import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_match_exceptions.PasswordNotMatchException;
import lessons.lesson_9_spring_boot.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.*;

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
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        try {
            boolean loggedIn = userService.checkPasswordByUserName(request.getUserName(),
                    request.getPassword());
            if (!loggedIn) {
                return badRequest().header("Warning", "Incorrect password").build();
            }

            UserEntity user = userService.findByUserName(request.getUserName());

            return ok(converterToLoginResponse.convert(user));
        } catch (UserNotFoundException e) {
           return notFound().build();
        } catch (PasswordNotMatchException e) {
            // max password try exhausted
            // 409 status set for simplicity
            return new ResponseEntity<>(null, HttpStatus.TOO_MANY_REQUESTS);
        }
    }

}
