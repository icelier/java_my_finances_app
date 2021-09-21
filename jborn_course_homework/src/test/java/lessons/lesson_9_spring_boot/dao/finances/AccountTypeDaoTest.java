package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.AccountType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class AccountTypeDaoTest {
    @Autowired private AccountTypeDao subj;
    @Autowired private AccountDao accountDao;

    private AccountType accountType;

    @Before
    public void setUp() throws Exception {
        accountType = new AccountType("new type");
        subj.save(accountType);

        subj.detach(accountType);
    }

    @Test
    public void findByTitle_ok() {
        assertNotNull(subj.findByTitle("new type").orElse(null));
    }

    @Test
    public void findByTitle_passUnknownTitle_returnNull() {
        assertNull(subj.findByTitle("unknown type").orElse(null));
    }

    @Test
    public void updateAccountTypeById_ok() {
        subj.updateAccountTypeById("new title", accountType.getId());

        assertEquals("new title", subj.getById(accountType.getId()).getTitle());
    }

    @Test
    public void deleteAccountTypeById_ok() {
        subj.deleteAccountTypeById(accountType.getId());

        assertNull(subj.findById(accountType.getId()).orElse(null));
    }

    @Test
    public void deleteAllAccountTypes_ok() {
        // to eliminate ConstraintViolationException
        accountDao.deleteAllAccounts();

        subj.deleteAllAccountTypes();

        assertEquals(0, subj.findAll().size());
    }
}