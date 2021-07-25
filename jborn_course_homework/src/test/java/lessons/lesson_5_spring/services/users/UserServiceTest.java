package lessons.lesson_5_spring.services.users;

import lessons.lesson_5_spring.entities.users.Role;
import lessons.lesson_5_spring.entities.users.UserEntity;
import lessons.lesson_5_spring.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_5_spring.exceptions.not_found_exception.RoleNotFoundException;
import lessons.lesson_5_spring.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_5_spring.exceptions.not_match_exceptions.PasswordNotMatchException;
import lessons.lesson_5_spring.exceptions.not_match_exceptions.QueryNotMatchException;
import lessons.lesson_5_spring.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_5_spring.terminal_views.TerminalConfiguration;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@RunWith(MockitoJUnitRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {
    Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
    UserService subj;
    static ApplicationContext context;

    @BeforeAll
    static void init() {
        System.setProperty("jdbcUrl", "jdbc:h2:mem:test_database?currentSchema=finances");
        System.setProperty("jdbcUsername", "admin");
        System.setProperty("jdbcPassword", "admin");
    }

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(TerminalConfiguration.class);
        subj = context.getBean(UserService.class);
    }

    @Test
    void findByUsernameAndPassword_ok() throws SQLException, PasswordNotMatchException {
        String username = "daddy";
        String password = "123";
        assertNotNull(subj.findByUserNameAndPassword(username, password));
    }

    @Test
    void findByUsernameAndPassword_wrongPassword() throws SQLException, PasswordNotMatchException {
        String username = "daddy";
        String password = "000";
        assertNull(subj.findByUserNameAndPassword(username, password));
    }

    @Test
    @Order(value = 1)
    void insert_ok() throws SQLException, UserAlreadyExistsException, QueryNotMatchException, OperationFailedException, RoleNotFoundException {
        List<UserEntity> users = subj.findAll();
        for (UserEntity user: users) {
            logger.debug(user.toString());
        }
        UserEntity user =  new UserEntity(
                    "user1",
                    "User User",
                    "123",
                    "user1@mail.ru",
                    15
            );
        user = subj.insert(user);
        assertNotNull(user.getId());
            logger.debug("Inserted user id = " + user.getId());
    }
    @Test
    @Order(value = 1)
    void insert_checkRoleUser() throws SQLException, UserAlreadyExistsException, QueryNotMatchException, OperationFailedException, RoleNotFoundException {
        UserEntity user =  new UserEntity(
                "user2",
                "User User",
                "123",
                "user2@mail.ru",
                15
        );
        user = subj.insert(user);
        assertNotNull(user.getRoles());
        assertFalse(user.getRoles().isEmpty());
        assertTrue(user.getRoles().contains(new Role(1L, "ROLE_USER")));
        logger.debug("Inserted user id = " + user.getId());
    }

    @Test
    @Order(value = 2)
    void insert_userAlreadyExists() {
        UserEntity user =  new UserEntity(
                "user1",
                "User User",
                "123",
                "user1@mail.ru",
                15
        );
        assertThrows(UserAlreadyExistsException.class,  () -> subj.insert(user));
    }

    @Test
    @Order(value = 3)
    void findAll_ok() throws SQLException {
        assertFalse(subj.findAll().isEmpty());
    }

    @Test
    @Order(value = 4)
    void update_ok() throws SQLException, UserNotFoundException, OperationFailedException, PasswordNotMatchException {
        UserEntity user = subj.findById(3L);
        user.setUserName("user22");
        subj.update(user);
        assertNotNull(subj.findByUserNameAndPassword("user22", "123"));
    }

    @Test
    @Order(value = 5)
    void delete_ok() throws SQLException, OperationFailedException {
        UserEntity user = subj.findById(3L);
        assertNotNull(user);
        subj.delete(user);
        assertNull(subj.findById(3L));
    }


}