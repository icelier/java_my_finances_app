package lessons.lesson_9_spring_boot.controllers.users.registration;

import lessons.lesson_9_spring_boot.controllers.users.login.LoginController;
import lessons.lesson_9_spring_boot.controllers.users.login.LoginRequest;
import lessons.lesson_9_spring_boot.controllers.users.login.LoginResponse;
import lessons.lesson_9_spring_boot.dao.users.RoleDao;
import lessons.lesson_9_spring_boot.entities.users.Role;
import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_9_spring_boot.services.users.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@RunWith(SpringRunner.class)
public class RegistrationControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private UserService userService;
    @MockBean RoleDao roleDao;
    private UserEntity registeredUser;

    @Before
    public void setUp() throws Exception {
        registeredUser = new UserEntity(
                "new user",
                "456",
                "newuser@gmail.com"
        );
    }

    @Test
    public void register_getStatusCreated() throws Exception {
        when(roleDao.findByName("ROLE_USER")).thenReturn(Optional.of(
           new Role(1L, "ROLE_USER")
        ));
        when(userService.insert(registeredUser)).then(
                (invocationOnMock) -> {
                    registeredUser.setId(50L);
                    return registeredUser;
                }
        );

        mockMvc.perform(
                post("/my-finances/users/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\n" +
                        "  \"userName\": \"newUser\",\n" +
                        "  \"password\": \"456\",\n" +
                        "  \"email\": \"newuser@gmail.com\"\n" +
                        "}")
        ).andExpect(status().isCreated());
    }

    @Test
    public void register_passNotEnoughData_getBadRequestStatus() throws Exception {
        mockMvc.perform(
                post("/my-finances/users/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"password\": \"456\",\n" +
                                "  \"email\": \"newuser@gmail.com\"\n" +
                                "}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void register_passBlankData_getBadRequestStatus() throws Exception {
        mockMvc.perform(
                post("/my-finances/users/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"userName\": \"\",\n" +
                                "  \"password\": \"456\",\n" +
                                "  \"email\": \"newuser@gmail.com\"\n" +
                                "}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void register_passRegisteredUser_throwsUserAlreadyExistsException_usedForLogin_getStatusOk() throws Exception {
        UserEntity user = new UserEntity(
                "daddy",
                "123",
                "sokol@gmail.com"
        );

        when(userService.insert(user)).thenThrow(UserAlreadyExistsException.class);

        mockMvc.perform(
                post("/my-finances/users/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"userName\": \"daddy\",\n" +
                                "  \"password\": \"123\",\n" +
                                "  \"email\": \"sokol@gmail.com\"\n" +
                                "}")
        )
                .andExpect(status().isSeeOther());
    }
}