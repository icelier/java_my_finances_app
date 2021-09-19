package lessons.lesson_9_spring_boot.controllers.users.registration;

import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_9_spring_boot.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

import java.net.URI;

@RequestMapping("/my-finances/users")
@RequiredArgsConstructor
@RestController
public class RegistrationController {

    private final UserService userService;

    @PostMapping(
            path = "/registration",
            consumes = { MediaType.APPLICATION_JSON_VALUE},
            produces = { MediaType.TEXT_PLAIN_VALUE })
    public ResponseEntity<String> register(@RequestBody @Valid RegistrationRequest request) {
        UserEntity newUser = new UserEntity(request.getUserName(),
                request.getPassword(),
                request.getEmail());
        try {
            newUser = userService.insert(newUser);

            String newUserAccountsUri = "http://localhost:8080/my-finances/accounts/?userId=" + newUser.getId();
            return ResponseEntity
                    .created(URI.create(newUserAccountsUri))
                    .body("Registration success! " +
                            "Now you can go to your accounts page: " + newUserAccountsUri);
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();

            // for simplicity
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .body("User already registered, try to login: http://localhost:8080/my-finances/users/login");
        }
    }

}
