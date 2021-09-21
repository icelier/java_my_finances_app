package lessons.lesson_9_spring_boot.controllers.users.login;

import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_match_exceptions.PasswordNotMatchException;
import lessons.lesson_9_spring_boot.services.users.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@RunWith(SpringRunner.class)
public class LoginControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockBean private UserService userService;
    @MockBean private UserToLoginResponseConverter converter;

    private UserEntity loggedInUser;

    @Before
    public void setUp() throws Exception {

        loggedInUser = new UserEntity(
                "daddy",
                "123",
                "sokol@gmail.com"
        );

        when(userService.findByUserName("daddy")).then(
                (invocationOnMock) -> {
                    loggedInUser.setId(1L);
                    return loggedInUser;
                }
        );
    }

    @Test
    public void login_getStatusOk() throws Exception {
        when(userService.checkPasswordByUserName(
                loggedInUser.getUserName(),
                loggedInUser.getPassword()
                )).thenReturn(true);

        when(converter.convert(loggedInUser)).thenReturn(
                new LoginResponse(
                        loggedInUser.getUserName(),
                        loggedInUser.getUserName(),
                        50,
                        "email",
                        Collections.emptyList()
                        )
        );

        mockMvc.perform(
                post("/my-finances/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"userName\": \"daddy\",\n" +
                                "  \"password\": \"123\"\n" +
                                "}")
        ).andExpect(status().isOk());
    }

    @Test
    public void login_passNotEnoughData_getBadRequestStatus() throws Exception {
        mockMvc.perform(
                post("/my-finances/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"password\": \"123\"\n" +
                                "}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void execute_passBlankData_getBadRequestStatus() throws Exception {
        mockMvc.perform(
                post("/my-finances/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"userName\": \"\",\n" +
                                "  \"password\": \"123\"\n" +
                                "}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void login_passIncorrectUserName_throwsUserNotFoundException_getBadRequestStatus() throws Exception {
        when(userService.checkPasswordByUserName(
                "addy","123"
        )).thenThrow(UserNotFoundException.class);

        mockMvc.perform(
                post("/my-finances/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"userName\": \"addy\",\n" +
                                "  \"password\": \"123\"\n" +
                                "}")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void login_passIncorrectPassword_getBadRequestStatus() throws Exception {
        when(userService.checkPasswordByUserName(
                "daddy","111"
        )).thenReturn(false);

        mockMvc.perform(
                post("/my-finances/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"userName\": \"daddy\",\n" +
                                "  \"password\": \"111\"\n" +
                                "}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void login_throwsPasswordNotMatchException_getTooManyRequestsStatus() throws Exception {
        when(userService.checkPasswordByUserName(
                "daddy","222"
        )).thenThrow(PasswordNotMatchException.class);

        mockMvc.perform(
                post("/my-finances/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"userName\": \"daddy\",\n" +
                                "  \"password\": \"222\"\n" +
                                "}")
        ).andExpect(status().isTooManyRequests());
    }
}