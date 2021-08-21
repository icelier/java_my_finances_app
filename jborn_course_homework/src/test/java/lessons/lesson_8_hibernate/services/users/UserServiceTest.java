package lessons.lesson_8_hibernate.services.users;

import lessons.lesson_8_hibernate.dao.users.RoleDao;
import lessons.lesson_8_hibernate.entities.users.Role;
import lessons.lesson_8_hibernate.entities.users.UserEntity;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.RoleNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_match_exceptions.PasswordNotMatchException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_8_hibernate.services.ServiceConfiguration;
import lessons.lesson_8_hibernate.services.finances.AccountService;
import lessons.lesson_8_hibernate.services.finances.CategoryService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {
    Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
    ApplicationContext context;
    UserService subj;
    AccountService accountService;
    CategoryService categoryService;
    EntityManager entityManager;
    RoleDao roleDao;
    BCryptPasswordEncoder encoder;

    @BeforeAll
    static void init() {
        System.setProperty("jdbcUrl", "jdbc:h2:mem:test_database?currentSchema=finances");
        System.setProperty("jdbcUsername", "admin");
        System.setProperty("jdbcPassword", "admin");
        System.setProperty("jdbcDriver", "org.h2.Driver");
        System.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    }

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(ServiceConfiguration.class);
        subj = context.getBean(UserService.class);
        accountService = context.getBean(AccountService.class);
        categoryService = context.getBean(CategoryService.class);
        entityManager = context.getBean(EntityManager.class);
        roleDao = context.getBean(RoleDao.class);
        encoder = new BCryptPasswordEncoder();
    }

    @Test
    @Order(1)
    void insert_ok() throws OperationFailedException, RoleNotFoundException, UserAlreadyExistsException {
        UserEntity newUser = new UserEntity(
                "NewUserName",
                "456",
                "newuser@mail.ru"
        );
        Role role = roleDao.findByName("ROLE_USER");
        newUser.setRoles(new ArrayList<>());
        newUser.getRoles().add(role);


        UserEntity insertedUser = subj.insert(newUser);

        assertNotNull(newUser.getId());
        logger.info("Inserted transaction = " + newUser);
    }

    @Test
    @Order(2)
    void insert_NotUniqueName_UserAlreadyExistsExceptionThrown() throws OperationFailedException, RoleNotFoundException, UserAlreadyExistsException {
        UserEntity newUser = new UserEntity(
                "NewUserName",
                "456",
                "newemail@mail.ru"
        );
        Role role = roleDao.findByName("ROLE_USER");
        newUser.setRoles(new ArrayList<>());
        newUser.getRoles().add(role);

        assertThrows(UserAlreadyExistsException.class, () -> subj.insert(newUser));
    }

    @Test
    @Order(3)
    void insert_NotUniqueEmail_UserAlreadyExistsExceptionThrown() throws OperationFailedException, RoleNotFoundException, UserAlreadyExistsException {
        UserEntity newUser = new UserEntity(
                "NewNewUserName",
                "456",
                "newuser@mail.ru"
        );
        Role role = roleDao.findByName("ROLE_USER");
        newUser.setRoles(new ArrayList<>());
        newUser.getRoles().add(role);

        assertThrows(UserAlreadyExistsException.class, () -> subj.insert(newUser));
    }

    @Test
    @Order(4)
    void findById_ok() throws OperationFailedException, RoleNotFoundException, UserAlreadyExistsException {
        UserEntity newUser = new UserEntity(
                "New_UserName",
                "456",
                "newUser@mail.ru"
        );
        Role role = roleDao.findByName("ROLE_USER");
        newUser.setRoles(new ArrayList<>());
        newUser.getRoles().add(role);

        UserEntity insertedUser = subj.insert(newUser);
        entityManager.detach(insertedUser);
        UserEntity userFromDb = subj.findById(insertedUser.getId());

        assertNotNull(userFromDb);
    }

    @Test
    @Order(4)
    void findByUserName_ok() throws OperationFailedException, UserNotFoundException {
        UserEntity userOne = subj.findById(1L);

        UserEntity userFromDb = subj.findByUserName(userOne.getUserName());

        assertNotNull(userFromDb);
    }

    @Test
    @Order(4)
    void findByUserNameANdPassword_ok() throws OperationFailedException, UserNotFoundException, PasswordNotMatchException {
        UserEntity userOne = subj.findById(1L);

        UserEntity userFromDb = subj.findByUserNameAndPassword(userOne.getUserName(), "123");

        assertNotNull(userFromDb);
    }

    @Test
    @Order(5)
    void delete_ok() throws OperationFailedException, DataNotFoundException, UserAlreadyExistsException {
        UserEntity newUser = new UserEntity(
                "New_User_Name",
                "456",
                "new_user@mail.ru"
        );
        Role role = roleDao.findByName("ROLE_USER");
        newUser.setRoles(new ArrayList<>());
        newUser.getRoles().add(role);

        UserEntity insertedUser = subj.insert(newUser);
        logger.debug("Inserted user = " + insertedUser);
        subj.delete(insertedUser);

        assertNull(subj.findById(insertedUser.getId()));
    }

    @Test
    @Order(6)
    void update_ok() throws OperationFailedException, UserNotFoundException {
        UserEntity user = subj.findById(1L);
        entityManager.detach(user);
        String newUsername = "NewName";
        String newPassword = "NewUserPassword";
        String newFullName = "NewFullName";
        int newAge = 60;
        String newEmail = "newEmail@mail.ru";
        List<Role> roles = new ArrayList<>();

        user.setUserName(newUsername);
        user.setPassword(newPassword);
        user.setFullName(newFullName);
        user.setAge(newAge);
        user.setEmail(newEmail);
        user.setRoles(roles);

        subj.update(user);
        entityManager.detach(user);

        UserEntity updatedUser = subj.findById(1L);
        assertEquals(newUsername, updatedUser.getUserName());
        assertEquals(newFullName, updatedUser.getFullName());
        assertEquals(newAge, updatedUser.getAge());
        assertTrue(encoder.matches(newPassword, updatedUser.getPassword()));
        assertEquals(roles, updatedUser.getRoles());
        assertNotEquals(newEmail, updatedUser.getEmail());
    }

    @Test
    @Order(7)
    void deleteAll_ok() throws OperationFailedException {
        List<UserEntity> users = subj.findAll();
        int deletedRows = subj.deleteAll();

        assertEquals(users.size(), deletedRows);
    }

    @Test
    @Order(8)
    void findAll_ok() throws OperationFailedException, RoleNotFoundException, UserAlreadyExistsException {
        UserEntity newUser1 = new UserEntity(
                "NewUserName1",
                "456",
                "newuser1@mail.ru"
        );
        Role role1 = roleDao.findByName("ROLE_USER");
        newUser1.setRoles(new ArrayList<>());
        newUser1.getRoles().add(role1);

        UserEntity newUser2 = new UserEntity(
                "NewUserName2",
                "456",
                "newuser2@mail.ru"
        );
        Role role2 = roleDao.findByName("ROLE_USER");
        newUser1.setRoles(new ArrayList<>());
        newUser1.getRoles().add(role2);

        subj.deleteAll();
        subj.insert(newUser1);
        subj.insert(newUser2);
        List<UserEntity> users = subj.findAll();

        assertEquals(2, users.size());
    }

}