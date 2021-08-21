package lessons.lesson_8_hibernate.dao.users;

import lessons.lesson_8_hibernate.dao.AbstractDao;
import lessons.lesson_8_hibernate.entities.users.UserEntity;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.DataAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;

@Repository
public class UserDao extends AbstractDao<UserEntity, Long> {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    private final EntityManager entityManager;

    public UserDao(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public UserEntity findById(Long id) throws OperationFailedException {
        logger.debug("UserDao entityManager = " + entityManager);
        UserEntity user;
        try {
            user = entityManager.find(UserEntity.class, id);
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return user;
    }

    public UserEntity findByUserName(String userName) throws UserNotFoundException, OperationFailedException {
        UserEntity user;
        try {
            Query query = entityManager.createQuery(getFindByUserNameQuery(), UserEntity.class);
            query.setParameter("userName", userName);
            user = (UserEntity) query.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return user;
    }

    public UserEntity findByEmail(String email) throws UserNotFoundException, OperationFailedException {
        UserEntity user;
        try {
            Query query = entityManager.createQuery(getFindByEmailQuery(), UserEntity.class);
            query.setParameter("email", email);
            user = (UserEntity) query.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return user;
    }

    @Override
    public List<UserEntity> findAll() throws OperationFailedException {
        List<UserEntity> users;
        try {
            TypedQuery<UserEntity> query = entityManager.createQuery(getFindAllQuery(), UserEntity.class);
            users = query.getResultList();
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return users;
    }

    @Override
    public UserEntity insert(UserEntity user) throws OperationFailedException, UserAlreadyExistsException {
        try {
            super.insert(user);
        } catch (DataAlreadyExistsException e) {
            throw new UserAlreadyExistsException(e.getMessage());
        }

        return user;
    }

    @Override
    public UserEntity update(UserEntity user) throws UserNotFoundException, OperationFailedException {
        boolean hasSuccess = false;
        EntityTransaction transaction = null;
        UserEntity userFromDb = null;

        while(!hasSuccess) {
            transaction = entityManager.getTransaction();
            transaction.begin();

            userFromDb = findById(user.getId());
            if (userFromDb == null) {
                throw new UserNotFoundException("User " + user.getUserName() + " not found in the database");
            }
            entityManager.lock(userFromDb, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            userFromDb = executeUpdateQuery(userFromDb, user);
            try {
                transaction.commit();
                hasSuccess = true;
            } catch (OptimisticLockException e) {
                e.printStackTrace();
                if (transaction.isActive()) {
                    try {
                        transaction.rollback();
                    } catch (Exception ex) {
                        throw new OperationFailedException(ex.getMessage());
                    }
                }
            }
        }

        return userFromDb;
    }

    @Override
    public void delete(UserEntity user) throws OperationFailedException, DataNotFoundException {
        UserEntity userFromDb = findById(user.getId());
        if (userFromDb == null) {
            throw new UserNotFoundException("User " + user.getUserName() +  " not found in the database");
        }
        super.delete(user);
    }

    @Override
    public int deleteAll() throws OperationFailedException {
        int deletedRows = 0;
        List<UserEntity> users = findAll();
        if (!users.isEmpty()) {
            deletedRows = super.deleteAll();
        }

        return deletedRows;
    }

    @Override
    protected String getFindByIdQuery() {
        return "SELECT u FROM UserEntity u WHERE u.id=:id";
    }

    private String getFindByUserNameQuery() {
        return "SELECT u FROM UserEntity u WHERE u.userName=:userName";
    }

    private String getFindByEmailQuery() {
        return "SELECT u FROM UserEntity u WHERE u.email=:email";
    }

    @Override
    protected String getFindAllQuery() {
        return "FROM UserEntity u ORDER BY u.id ASC";
    }

    @Override
    protected String getDeleteAllQuery() {
        return "DELETE FROM UserEntity u";
    }

    @Override
    public void updateDomain(UserEntity persistentUser, UserEntity user) {
        persistentUser.setUserName(user.getUserName());
        persistentUser.setFullName(user.getFullName());
        persistentUser.setPassword(user.getPassword());
        persistentUser.setAge(user.getAge());
        persistentUser.setRoles(user.getRoles());
    }
}
