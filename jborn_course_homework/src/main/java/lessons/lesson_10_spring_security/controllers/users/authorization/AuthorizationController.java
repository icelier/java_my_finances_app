package lessons.lesson_10_spring_security.controllers.users.authorization;

import lessons.lesson_10_spring_security.MyApplication;
import lessons.lesson_10_spring_security.entities.users.UserEntity;
import lessons.lesson_10_spring_security.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_10_spring_security.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import static lessons.lesson_10_spring_security.MyApplication.BASE_URL;

@RequestMapping(BASE_URL + "/users")
@RequiredArgsConstructor
@Controller
public class AuthorizationController {
    private final UserService userService;

    // TODO - add test
    @GetMapping(path = "/authorize")
    public String getRegistrationPage() {
        return "registration";
    }

    // TODO - add test
    @GetMapping(path = "/login")
    public String getLoginPage() {
        return "login";
    }

    @PostMapping(
            path = "/register")
    public String register(RedirectAttributes redirectAttrs,
                                           @ModelAttribute(name = "newUser") @Valid RegistrationRequest request) {
        if (blankInputData(request)) {
            return "redirect:" + BASE_URL + "/users/authorize?error";
        }

        UserEntity newUser = new UserEntity(request.getUserName(),
                request.getPassword(),
                request.getEmail());
        try {
            newUser = userService.insert(newUser);

            return "redirect:" + MyApplication.BASE_URL + "/users/login";
        } catch (UserAlreadyExistsException e) {
            redirectAttrs.addFlashAttribute("alreadyExists", true);

            return "redirect:" + MyApplication.BASE_URL + "/users/login";
        }
    }

    private boolean blankInputData(RegistrationRequest request) {
        return request.getUserName().isBlank() || request.getPassword().isBlank()
                || request.getEmail().isBlank();
    }

}
