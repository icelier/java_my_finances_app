package lessons.lesson_9_spring_boot.dao.users;

import lessons.lesson_9_spring_boot.entities.users.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class RoleDaoTest {
    @Autowired private RoleDao subj;
    @Autowired private UserDao userDao;

    private Role newRole;

    @Before
    public void setUp() {
        newRole = new Role("NEW_ROLE");
        newRole = subj.save(newRole);
        subj.detach(newRole);
    }

    @Test
    public void findByName_ok() {
        Role newRole2 = new Role("NEW_ROLE 2");
        subj.save(newRole2);
        subj.detach(newRole2);

        assertNotNull(subj.findByName("NEW_ROLE 2").orElse(null));
    }


    @Test
    public void findByName_passUnknownName_returnNull() {
        assertNull(subj.findByName("NEW_ROLE 2").orElse(null));
    }

    @Test
    public void updateRoleById_ok() {
        subj.updateRoleById("new name", newRole.getId());

        assertEquals("new name", subj.getById(newRole.getId()).getName());
    }

    @Test
    public void deleteRoleById_ok() {
        subj.deleteRoleById(newRole.getId());

        assertNull(subj.findById(newRole.getId()).orElse(null));
    }

    @Test
    public void deleteAllRoles_ok() {
        // to eliminate ConstraintViolationException
        userDao.deleteAllUsers();

        subj.deleteAllRoles();

        assertEquals(0, subj.findAll().size());
    }
}