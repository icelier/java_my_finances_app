package lessons.lesson_9_spring_boot.dao.users;

import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class UserDaoTest {
    @Autowired private UserDao subj;

    private UserEntity newUser;

    @Before
    public void setUp() throws Exception {
        newUser = new UserEntity(
                "user name",
                "user fullName",
                "user password",
                "user@mail.ru",
                35
        );
        subj.save(newUser);
        subj.detach(newUser);
    }

    @Test
    public void findByUserName_ok() {
        assertNotNull(subj.findByUserName("user name").orElse(null));
    }

    @Test
    public void findByUserName_passUnknownUserName_returnNull() {
        assertNull(subj.findByUserName("unknown user name").orElse(null));
    }

    @Test
    public void findByEmail_ok() {
        assertNotNull(subj.findByEmail("user@mail.ru").orElse(null));
    }

    @Test
    public void findByEmail_passUnknownEmail_returnNull() {
        assertNull(subj.findByEmail("unknowm@mail.ru").orElse(null));
    }

    @Test
    public void updateUserNameById_ok() {
        subj.updateUserNameById("new name", newUser.getId());

        assertEquals("new name", subj.getById(newUser.getId()).getUserName());
    }

    @Test
    public void updateUserFullNameById_ok() {
        subj.updateUserFullNameById("new fullName", newUser.getId());

        assertEquals("new fullName", subj.getById(newUser.getId()).getFullName());
    }

    @Test
    public void updateUserAgeById_ok() {
        subj.updateUserAgeById(10, newUser.getId());

        assertEquals(10, subj.getById(newUser.getId()).getAge());
    }

    @Test
    public void updateUserPasswordById_ok() {
        subj.updateUserPasswordById("new password", newUser.getId());

        assertEquals("new password", subj.getById(newUser.getId()).getPassword());
    }

    @Test
    public void deleteUserById_ok() {
        subj.deleteUserById(newUser.getId());

        assertNull(subj.findById(newUser.getId()).orElse(null));
    }

    @Test
    public void deleteAllUsers_ok() {
        subj.deleteAllUsers();

        assertEquals(0, subj.findAll().size());
    }
}