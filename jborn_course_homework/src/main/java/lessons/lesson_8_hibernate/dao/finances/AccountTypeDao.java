package lessons.lesson_8_hibernate.dao.finances;

import lessons.lesson_8_hibernate.dao.AbstractDao;
import lessons.lesson_8_hibernate.entities.finances.AccountType;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.AccountTypeAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.DataAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.AccountTypeNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class AccountTypeDao extends AbstractDao<AccountType, Long> {
    private static final Logger logger = LoggerFactory.getLogger(AccountTypeDao.class);

    private final EntityManager entityManager;

    public AccountTypeDao(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public AccountType findById(Long id) throws OperationFailedException {
        logger.debug("AccounTypeDao entityManager = " + entityManager);
        AccountType accountType;
        try {
            accountType = entityManager.find(AccountType.class, id);
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return accountType;
    }

    @Override
    public List<AccountType> findAll() throws OperationFailedException {
        List<AccountType> accountTypes;
        try {
            TypedQuery<AccountType> query = entityManager.createQuery(getFindAllQuery(), AccountType.class);
            accountTypes = query.getResultList();
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return accountTypes;
    }

    @Override
    public AccountType insert(AccountType accountType) throws AccountTypeAlreadyExistsException, OperationFailedException {
        try {
            super.insert(accountType);
        } catch (DataAlreadyExistsException e) {
            throw new AccountTypeAlreadyExistsException(e.getMessage());
        }

        return accountType;
    }

    @Override
    public AccountType update(AccountType accountType) throws AccountTypeNotFoundException, OperationFailedException {
        entityManager.getTransaction().begin();
        AccountType accountTypeFromDb = findById(accountType.getId());
        if (accountTypeFromDb == null) {
            throw new AccountTypeNotFoundException("Account type " + accountType.getTitle() + " not found in the database");
        }
        accountTypeFromDb = executeUpdateQuery(accountTypeFromDb, accountType);
        entityManager.getTransaction().commit();

        return accountTypeFromDb;
    }

    @Override
    public void delete(AccountType accountType) throws OperationFailedException, DataNotFoundException {
        AccountType accountTypeFromDb = findById(accountType.getId());
        if (accountTypeFromDb == null) {
            throw new AccountTypeNotFoundException("Account type " + accountType.getTitle() + " not found in the database");
        }
        super.delete(accountType);
    }

    @Override
    public int deleteAll() throws OperationFailedException {
        List<AccountType> accountTypes = findAll();
        int deletedRows = 0;
        if (!accountTypes.isEmpty()) {
            deletedRows = super.deleteAll();
        }

        return deletedRows;
    }

    @Override
    protected String getFindByIdQuery() {
        return "SELECT at FROM AccountType at WHERE at.id=:id";
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT at FROM AccountType at ORDER BY at.id ASC";
    }

    @Override
    protected String getDeleteAllQuery() {
        return "DELETE FROM AccountType at";
    }

    @Override
    public void updateDomain(AccountType persistentType, AccountType accountType) {
        persistentType.setTitle(accountType.getTitle());
    }
}
