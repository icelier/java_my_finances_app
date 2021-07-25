package lessons.lesson_5_spring.services.users;

import lessons.lesson_5_spring.dao.users.UserDao;
import lessons.lesson_5_spring.entities.users.UserEntity;
import lessons.lesson_5_spring.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_5_spring.exceptions.not_found_exception.RoleNotFoundException;
import lessons.lesson_5_spring.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_5_spring.exceptions.not_match_exceptions.PasswordNotMatchException;
import lessons.lesson_5_spring.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_5_spring.services.AbstractService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class UserService implements AbstractService<UserEntity, Long> {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private UserDao userDao;

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
     * Inserts new user entity into database, checking if already exists in the database by unique username and unique email
     * Encodes password before insertion
     * @param user entity to insert into database
     * @return user entity with generated id
     * @throws UserAlreadyExistsException if user found in the database by username or email
     * @throws SQLException if database access error occurred, if underlying query failed
     * @throws OperationFailedException if key generation for inserted user failed,
     * if insertion of ROLE_USER for given user failed
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

    @Override
    public UserEntity update(UserEntity user) throws SQLException, UserNotFoundException, OperationFailedException {
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
     * @param password to check for match
     * @param user entity to be referenced given password against
     * @return true if both password encoded with BCryptEncoder match or else false
     */
    public boolean checkPasswordCorrect(String password, UserEntity user) {
        return encoder.matches(password, user.getPassword());
    }
}
