package lessons.lesson_7_controllers.services.users;

import lessons.lesson_7_controllers.dao.users.UserDao;
import lessons.lesson_7_controllers.entities.users.Role;
import lessons.lesson_7_controllers.entities.users.UserEntity;
import lessons.lesson_7_controllers.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_7_controllers.exceptions.not_found_exception.RoleNotFoundException;
import lessons.lesson_7_controllers.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_7_controllers.exceptions.not_match_exceptions.PasswordNotMatchException;
import lessons.lesson_7_controllers.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_7_controllers.services.AbstractService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class UserService implements AbstractService<UserEntity, Long> {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserEntity findById(Long id) throws SQLException {
        return userDao.findById(id);
    }

    @Override
    public List<UserEntity> findAll() throws SQLException {
        return userDao.findAll();
    }

    /**
     * Inserts new user entity into database, checking first if already exists in the database by unique email and
     * then by unique username
     * Encodes password before insertion
     * @param user entity to insert into database
     * @return user entity with generated id
     * @throws UserAlreadyExistsException if user found in the database by username or email
     * @throws SQLException if database access error occurred, if underlying query failed
     * @throws OperationFailedException if id generation for inserted user failed,
     * if id generation for ROLE_USER for given user failed, if ROLE_USER for given user id already exists in the database
     */
    @Override
    public UserEntity insert(UserEntity user) throws UserAlreadyExistsException, SQLException, OperationFailedException {
        UserEntity userFromDbByEmail = userDao.findByEmail(user.getEmail());
        if (userFromDbByEmail != null) {
            throw new UserAlreadyExistsException("Email already registered");
        }
        UserEntity userFomDbByUsername = userDao.findByUserName(user.getUserName());
        if (userFomDbByUsername != null) {
            throw new UserAlreadyExistsException("User by this username has already been registered");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        try {
            user = userDao.insert(user);
        } catch (RoleNotFoundException e) {
            throw new OperationFailedException("Failed to insert new user, couldn't find ROLE_USER in the database");
        }

        return user;
    }

    /**
     * Returns updated user, encoding new password before update.
     * Updates related table users_roles with many-to-many relation in the same transaction.
     * If transaction fails at any stage, makes transaction rollback
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     * @throws UserNotFoundException if given user not found in the database
     * @throws OperationFailedException if update of user roles failed  (either deletion of unnecessary roles
     * or insertion of new roles into many-to-many related table users_roles)
     * @return updated user entity if success
     */
    @Override
    public UserEntity update(UserEntity user) throws SQLException, UserNotFoundException, OperationFailedException {
        String password = user.getPassword();
        user.setPassword(encoder.encode(password));

        return userDao.update(user);
    }

    @Override
    public void delete(UserEntity user) throws SQLException, OperationFailedException {
        userDao.delete(user);
    }

    /**
     * Returns user entity found in database by username and password provided or null if not found
     * @param name for username column in the table
     * @param password column in the table
     * @return user entity found by username and password or null
     * @throws SQLException if database access error occurred, if underlying query failed
     * @throws PasswordNotMatchException if username found but password does not match
     */
    public UserEntity findByUserNameAndPassword(String name, String password) throws SQLException, PasswordNotMatchException {
        UserEntity user = null;
        user = userDao.findByUserName(name);

        if (user == null) {
            return null;
        }
        if (!checkPasswordCorrect(password, user)) {
            throw new PasswordNotMatchException("Password does not correspond to this username");
        }

        return user;
    }

    /**
     * Returns user entity found by given username in database or null if no any
     * with roles obtained in a separate query
     * @param name for search user in the database
     * @return user entity or null
     * @throws SQLException if database access error occurred, if underlying query failed
     */
    public UserEntity findByUserName(String name) throws SQLException {
        return userDao.findByUserName(name);
    }

    /**
     * Checks provided password and password from the given user entity with BCryptEncoder
     * @param passwordTry to check for match
     * @param originUserEntity entity containing the user entity to be the reference to check password match
     * @return true if both password encoded with BCryptEncoder match or else false
     */
    public boolean checkPasswordCorrect(String passwordTry, UserEntity originUserEntity) {
        return encoder.matches(passwordTry, originUserEntity.getPassword());
    }

    public List<Role> getUserRoles(Long userId) throws SQLException {
        return userDao.getUserRoles(userId);
    }
}
