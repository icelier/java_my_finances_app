package lessons.lesson_9_spring_boot.controllers.users.registration;

import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_9_spring_boot.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RequestMapping("/my-finances/users")
@RequiredArgsConstructor
@RestController
public class RegistrationController {
    private final UserService userService;

    @PostMapping(
            path = "/registration",
            consumes = { MediaType.APPLICATION_JSON_VALUE},
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> execute(@RequestBody RegistrationRequest request) {
        UserEntity newUser = new UserEntity(request.getUserName(),
                request.getPassword(),
                request.getEmail());
        try {
            newUser = userService.insert(newUser);
            return ok("Successfully registered! Your id = " + newUser.getId());
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();
            return ok("redirect:/login");
        }
    }

}
