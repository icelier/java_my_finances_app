package lessons.lesson_9_spring_boot.services.users;

import lessons.lesson_9_spring_boot.dao.users.RoleDao;
import lessons.lesson_9_spring_boot.dao.users.UserDao;
import lessons.lesson_9_spring_boot.entities.users.Role;
import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_match_exceptions.PasswordNotMatchException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class UserServiceTest {
    @SpyBean private UserService subj;

    @MockBean private UserDao userDao;
    @MockBean RoleDao roleDao;
    @MockBean private MyPasswordEncoder encoder;

    private UserEntity user;

    @Before
    public void setUp() {
        user = new UserEntity(
                100L,
                "user name",
                "user fullName",
                "user password",
                "user@mail.ru",
                35
        );

    }

    @Test
    public void findById_ok() {
        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));

        assertNotNull(subj.findById(user.getId()));
    }

    @Test
    public void findById_passUnknownId_returnNull() {
        when(userDao.findById(100L)).thenReturn(Optional.empty());

        assertNull(subj.findById(100L));
    }

    @Test
    public void findByUserName_ok() {
        when(userDao.findByUserName(user.getUserName())).thenReturn(Optional.of(user));

        assertNotNull(subj.findByUserName("user name"));
    }

    @Test
    public void findByUserName_passUnknownUserName_returnNull() {
        when(userDao.findByUserName("unknown name")).thenReturn(Optional.empty());

        assertNull(subj.findByUserName("unknown name"));
    }

    @Test
    public void findByEmail_ok() {
        when(userDao.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertNotNull(subj.findByEmail(user.getEmail()));
    }

    @Test
    public void findByEmail_passUnknownEmail_returnNull() {
        when(userDao.findByEmail("unknown@mail.ru")).thenReturn(Optional.empty());

        assertNull(subj.findByEmail("unknown@mail.ru"));
    }

    @Test
    public void findAll_ok() {
        List<UserEntity> users = new ArrayList<>();
        users.add(user);

        when(userDao.findAll()).thenReturn(users);

        assertEquals(1, subj.findAll().size());
    }

    @Test
    public void insert_ok() throws UserAlreadyExistsException {
        UserEntity user2 = new UserEntity(
                "username 2",
                "fullname 2",
                "password2",
                "user2@mail.ru",
                45
        );

        when(roleDao.findByName("ROLE_USER")).thenReturn(Optional.of(
                new Role(1L, "ROLE_USER")));
        when(userDao.findByEmail(user2.getEmail())).thenReturn(Optional.empty());
        when(userDao.findByUserName(user2.getUserName())).thenReturn(Optional.empty());
        when(encoder.encode("password2")).thenReturn("password2");
        UserEntity finalUser = user2;
        when(userDao.save(user2)).then(
                (invocation) -> {
                    finalUser.setId(100L);
                    return finalUser;
                }
        );
        user2 = subj.insert(user2);

        assertEquals(100L, (long) user2.getId());
        assertEquals(1, user2.getRoles().size());
        assertEquals("ROLE_USER", user2.getRoles().get(0).getName());
    }

    @Test
    public void insert_passSameUserName_throwsUserAlreadyExistsException() {
        UserEntity user2 = new UserEntity(
                "user name",
                "fullname 2",
                "password2",
                "user2@mail.ru",
                45
        );

        when(userDao.findByUserName(user2.getUserName())).thenReturn(Optional.of(user2));

        assertThrows(
                UserAlreadyExistsException.class,
                () -> subj.insert(user2)
        );
    }

    @Test
    public void insert_passSameEmail_throwsUserAlreadyExistsException() {
        UserEntity user2 = new UserEntity(
                "username 2",
                "fullname 2",
                "password2",
                "user@mail.ru",
                45
        );

        when(userDao.findByEmail(user2.getEmail())).thenReturn(Optional.of(user2));

        assertThrows(
                UserAlreadyExistsException.class,
                () -> subj.insert(user2)
        );
    }

    @Test
    public void update_ok_checkOnlyUpdatableFieldsUpdated() {
        UserEntity updateData = new UserEntity(
                "new username",
                "new fullname",
                "new password",
                "newemail@mail.ru",
                100
        );

        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));
        when(encoder.encode("new password")).thenReturn("new password");

        user = subj.update(user.getId(), updateData);
        assertEquals("new username", user.getUserName());
        assertEquals("new fullname", user.getFullName());
        assertEquals("new password", user.getPassword());
        assertEquals(100, user.getAge());
        assertNotEquals("newemail@mail.ru", user.getEmail());
    }

    @Test
    public void update_passUnknownId_throwsUserNotFoundException() {
        UserEntity updateData = new UserEntity(
                "new username",
                "new fullname",
                "new password",
                "newemail@mail.ru",
                100
        );

        when(userDao.findById(user.getId() + 1)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> subj.update(user.getId() + 1, updateData)
        );
    }

    @Test
    public void updateUserNameById_ok() {
        String newUserName = "new username";

        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));
        when(userDao.updateUserNameById(newUserName, user.getId())).then(
                (invocation) -> {
                    user.setUserName(newUserName);
                    return 1;
                }
        );

        user = subj.updateUserNameById(user.getId(), newUserName);

        assertEquals("new username", user.getUserName());
    }

    @Test
    public void updateUserNameById_passUnknownId_throwsUserNotFoundException() {
        String newUserName = "new username";

        when(userDao.findById(user.getId() + 1)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> subj.updateUserNameById(user.getId() + 1, newUserName)
        );
    }

    @Test
    public void updateFullNameById_ok() {
        String newFullName = "new fullname";

        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));
        when(userDao.updateUserFullNameById(newFullName, user.getId())).then(
                (invocation) -> {
                    user.setFullName(newFullName);
                    return 1;
                }
        );

        user = subj.updateFullNameById(user.getId(), newFullName);

        assertEquals("new fullname", user.getFullName());
    }

    @Test
    public void updateFullNameById_passUnknownId_throwsUserNotFoundException() {
        String newFullName = "new fullname";

        when(userDao.findById(user.getId() + 1)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> subj.updateFullNameById(user.getId() + 1, newFullName)
        );
    }

    @Test
    public void updatePasswordById_ok() {
        String newPassword = "new password";

        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));
        when(userDao.updateUserPasswordById(newPassword, user.getId())).then(
                (invocation) -> {
                    user.setPassword(newPassword);
                    return 1;
                }
        );
        when(encoder.encode("new password")).thenReturn("new password");

        user = subj.updatePasswordById(user.getId(), newPassword);

        assertEquals("new password", user.getPassword());
    }

    @Test
    public void updatePasswordById_passUnknownId_throwsUserNotFoundException() {
        String newPassword = "new password";

        when(userDao.findById(user.getId() + 1)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> subj.updatePasswordById(user.getId() + 1, newPassword)
        );
    }

    @Test
    public void updateAgeById_ok() {
        int newAge = 100;

        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));
        when(userDao.updateUserAgeById(newAge, user.getId())).then(
                (invocation) -> {
                    user.setAge(newAge);
                    return 1;
                }
        );

        user = subj.updateAgeById(user.getId(), newAge);

        assertEquals(newAge, user.getAge());
    }

    @Test
    public void updateAgeById_passUnknownId_throwsUserNotFoundException() {
        int newAge = 100;

        when(userDao.findById(user.getId() + 1)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> subj.updateAgeById(user.getId() + 1, newAge)
        );
    }


    @Test
    public void delete_ok() {
        doNothing().when(userDao).delete(user);

        when(userDao.findById(100L)).thenReturn(Optional.empty());

        subj.delete(user);

        assertNull(userDao.findById(100L).orElse(null));
    }

    @Test
    public void deleteById_ok() {
        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));
        when(userDao.deleteUserById(user.getId())).thenReturn(1);

        subj.deleteById(user.getId());

        when(userDao.findById(user.getId())).thenReturn(Optional.empty());

        assertNull(userDao.findById(user.getId()).orElse(null));
    }

    @Test
    public void deleteById_passUnknownId_throwsUserNotFoundException() {
        when(userDao.findById(user.getId() + 1)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> subj.deleteById(user.getId() + 1)
        );
    }

    @Test
    public void deleteAll_ok() {
        when(userDao.deleteAllUsers()).thenReturn(1);

        subj.deleteAll();

        when(userDao.findAll()).thenReturn(Collections.emptyList());

        assertEquals(0, userDao.findAll().size());
    }

    @Test
    public void checkPasswordByUserName_ok() throws PasswordNotMatchException {
        when(userDao.findByUserName(user.getUserName())).thenReturn(Optional.of(user));

        when(encoder.matches("user password", "user password"))
                .thenReturn(true);

        assertTrue(
                subj.checkPasswordByUserName(user.getUserName(), "user password")
        );
    }

    @Test
    public void checkPasswordByUserName_passUnknownUserName_throwsUserNotFoundException() {
        when(userDao.findByUserName("unknown userName")).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> subj.checkPasswordByUserName("unknown userName",
                        "user password")
        );
    }

    @Test
    public void checkPasswordByUserName_passWrongPasswordThreeTimes_throwsPasswordNotMatchException()
            throws PasswordNotMatchException {
        when(userDao.findByUserName(user.getUserName())).thenReturn(Optional.of(user));

        when(encoder.matches("wrong password", "user password"))
                .thenThrow(PasswordNotMatchException.class);

        assertThrows(
                PasswordNotMatchException.class,
                () -> {
                    subj.checkPasswordByUserName("user name", "wrong password");
                }
        );
    }

}