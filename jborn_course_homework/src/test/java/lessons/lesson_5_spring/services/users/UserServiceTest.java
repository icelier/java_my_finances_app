package lessons.lesson_5_spring.services.users;

import lessons.lesson_5_spring.dao.users.RoleDao;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {
    Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
    UserService subj;
    RoleDao roleDao;
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
        roleDao = context.getBean(RoleDao.class);
    }

    @Test
    @Order(value = 1)
    void findById_ok() throws SQLException {
        assertNotNull(subj.findById(1L));
    }

    @Test
    @Order(value = 2)
    void findById_userNotFound() throws SQLException {
        assertNull(subj.findById(100L));
    }

    @Test
    @Order(value = 3)
    void findAll_ok() throws SQLException {
        assertEquals(2, subj.findAll().size());
    }

    @Test
    @Order(value = 4)
    void findByUsernameAndPassword_ok() throws SQLException, PasswordNotMatchException {
        String username = "daddy";
        String password = "123";
        assertNotNull(subj.findByUserNameAndPassword(username, password));
    }

    @Test
    @Order(value = 5)
    void findByUsernameAndPassword_wrongUserName() throws SQLException, PasswordNotMatchException {
        String username = "dady";
        String password = "123";
        assertNull(subj.findByUserNameAndPassword(username, password));
    }

    @Test
    @Order(value = 6)
    void findByUsernameAndPassword_wrongPassword() {
        String username = "daddy";
        String password = "000";
        assertThrows(PasswordNotMatchException.class, () -> subj.findByUserNameAndPassword(username, password));
    }

    @Test
    @Order(value = 7)
    void insert_ok() throws SQLException, UserAlreadyExistsException, OperationFailedException {
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

    }

    @Test
    @Order(value = 8)
    void insert_userWithSuchUserNameAlreadyExists() {
        UserEntity user =  new UserEntity(
                "user1",
                "User User",
                "123",
                "user2@mail.ru",
                15
        );
        assertThrows(UserAlreadyExistsException.class,  () -> subj.insert(user));
    }

    @Test
    @Order(value = 9)
    void insert_userWithSuchEmailAlreadyExists() {
        UserEntity user =  new UserEntity(
                "user2",
                "User User",
                "123",
                "user1@mail.ru",
                15
        );
        assertThrows(UserAlreadyExistsException.class,  () -> subj.insert(user));
    }

    @Test
    @Order(value = 10)
    void insert_checkRoleUser() throws SQLException, UserAlreadyExistsException, OperationFailedException {
        UserEntity user =  new UserEntity(
                "user2",
                "User User",
                "123",
                "user2@mail.ru",
                15
        );
        user = subj.insert(user);
        assertNotNull(user.getRoles());
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(roleDao.findByName("ROLE_USER")));
    }

    @Test
    @Order(value = 11)
    void update_ok() throws SQLException, UserNotFoundException, OperationFailedException, PasswordNotMatchException {
        UserEntity user = subj.findById(3L);
        user.setUserName("user22");
        user.setFullName("Andrey Egorov");
        user.setAge(35);
        user.setEmail("a.egorov@mail.ru");
        user.setPassword("456");
        List<Role> roles = new ArrayList<>();
        roles.add(roleDao.findByName("ROLE_ADMIN"));
        user.setRoles(roles);
        subj.update(user);

        UserEntity userFromDb = subj.findById(3L);
        assertEquals(3L, subj.findByUserName("user22").getId());
        assertNotNull(subj.findByUserNameAndPassword("user22", "456"));
        assertEquals("Andrey Egorov", userFromDb.getFullName());
        assertEquals(35, userFromDb.getAge());
        assertEquals("a.egorov@mail.ru", userFromDb.getEmail());

        assertFalse(userFromDb.getRoles().contains(roleDao.findByName("ROLE_USER")));
    }

    @Test
    @Order(value = 12)
    void delete_ok() throws SQLException, OperationFailedException {
        UserEntity user = subj.findById(4L);
        assertNotNull(user);
        Collection<Role> userRoles = user.getRoles();
        assertEquals(1, userRoles.size());
        assertTrue(userRoles.contains(roleDao.findByName("ROLE_USER")));
        subj.delete(user);
        assertNull(subj.findById(4L));
        assertTrue(subj.getUserRoles(4L).isEmpty());
    }


}