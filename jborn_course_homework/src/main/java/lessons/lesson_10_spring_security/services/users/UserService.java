package lessons.lesson_10_spring_security.services.users;

import lessons.lesson_10_spring_security.controllers.users.authentication_utils.password_security.LoginAttemptService;
import lessons.lesson_10_spring_security.dao.users.RoleDao;
import lessons.lesson_10_spring_security.dao.users.UserDao;
import lessons.lesson_10_spring_security.entities.users.Role;
import lessons.lesson_10_spring_security.entities.users.UserEntity;
import lessons.lesson_10_spring_security.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_10_spring_security.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_10_spring_security.exceptions.not_match_exceptions.PasswordNotMatchException;
import lessons.lesson_10_spring_security.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_10_spring_security.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service("userDetailsService")
public class UserService implements AbstractService<UserEntity, Long>,
        UserDetailsService {

    @Autowired private final UserDao userDao;
    @Autowired private final RoleDao roleDao;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired private BCryptPasswordEncoder encoder;

    @Autowired
    private HttpServletRequest request;

    public UserService(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) {
        String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip)) {
            System.out.println("BLOCKED");
            throw new RuntimeException("blocked");
        }

        try {
            UserEntity user = userDao.findUser(userName).orElse(null);
            if (user == null) {
                return new User(
                        " ", " ",
                        true, true, true, true,
                        mapRolesToAuthorities(Collections.singletonList(
                                roleDao.findByName("ROLE_USER").orElse(null))
                        ));
            }

            return new User(
                    user.getUserName(), user.getPassword(),
                    true, true, true, true,
                    mapRolesToAuthorities(user.getRoles()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }

        return xfHeader.split(",")[0];
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(
            Collection<Role> roles) {

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public UserEntity findById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    public UserEntity findByUserName(String userName) {
        return userDao.findByUserName(userName).orElse(null);
    }

    public UserEntity findByEmail(String email) {
        return userDao.findByEmail(email).orElse(null);
    }

    @Override
    public List<UserEntity> findAll() {
        return userDao.findAll();
    }

    /**
     * Returns inserted entity with generated id.
     * First checks for email and then userName already exists.
     * If yes throws exception, otherwise proceeds with entity insertion.
     * Before insertion encodes password with BCryptEncoder.
     * Adds ROLE_USER to new user entity
     * @param user object to save in database
     * @return persistent entity inserted into database with generated id
     * @throws UserAlreadyExistsException if email or username already registered
     */
    @Transactional
    @Override
    public UserEntity insert(UserEntity user) throws UserAlreadyExistsException {
        UserEntity userFromDbByEmail = findByEmail(user.getEmail());
        if (userFromDbByEmail != null) {
            throw new UserAlreadyExistsException(
                    "Email " + user.getEmail() + " already registered"
            );
        }

        UserEntity userFomDbByUsername = findByUserName(user.getUserName());
        if (userFomDbByUsername != null) {
            throw new UserAlreadyExistsException(
                    "User by username " + user.getUserName() + "  has already been registered"
            );
        }

        user.setPassword(encoder.encode(user.getPassword()));
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleDao.findByName("ROLE_USER").orElse(null));
        user.setRoles(userRoles);

        user = userDao.save(user);

        return user;
    }

    /**
     * Retrieves an entity from database by given id. Throws exception, if no user found.
     * Encodes password with BCryptEncoder before update.
     * Updates only updatable fields from the given object.
     * Returns updated entity from database after update committed
     * @param id of user to update
     * @param user object containing update information
     * @return updated entity
     * @throws UserNotFoundException if user not found by given id
     */
    @Transactional
    @Override
    public UserEntity update(Long id, UserEntity user) {
        UserEntity userFromDb = findById(id);
        if (userFromDb == null) {
            throw new UserNotFoundException("User with id " + id +" not found");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        updateDomainWithNewData(userFromDb, user);

        detach(userFromDb);

        return findById(id);
    }

    /**
     * Retrieves an entity from database by given id. Throws exception, if no user found.
     * Updates userName into database.
     * Returns updated entity from database after update committed
     * @param id of user to update
     * @param newUserName for update
     * @return updated entity
     * @throws UserNotFoundException if user not found by given id
     * @throws OperationFailedException if update failed
     */
    @Transactional
    public UserEntity updateUserNameById(Long id, String newUserName) {
        UserEntity userFromDb = findById(id);
        if (userFromDb == null) {
            throw new UserNotFoundException("User with id " + id +" not found");
        }
        detach(userFromDb);

        int affectedRows = userDao.updateUserNameById(newUserName, id);
        if (affectedRows <= 0) {
            throw new OperationFailedException("Failed to update user data");
        }

        return findById(id);
    }

    /**
     * Retrieves an entity from database by given id. Throws exception, if no user found.
     * Updates fullName into database.
     * Returns updated entity from database after update committed
     * @param id of user to update
     * @param newFullName for update
     * @return updated entity
     * @throws UserNotFoundException if user not found by given id
     * @throws OperationFailedException if update failed
     */
    @Transactional
    public UserEntity updateFullNameById(Long id, String newFullName) {
        UserEntity userFromDb = findById(id);
        if (userFromDb == null) {
            throw new UserNotFoundException("User with id " + id +" not found");
        }
        detach(userFromDb);

        int affectedRows = userDao.updateUserFullNameById(newFullName, id);
        if (affectedRows <= 0) {
            throw new OperationFailedException("Failed to update user data");
        }

        return findById(id);
    }

    /**
     * Retrieves an entity from database by given id. Throws exception, if no user found.
     * Encodes password with BCryptEncoder before update.
     * Updates password into database.
     * Returns updated entity from database after update committed
     * @param id of user to update
     * @param newPassword for update
     * @return updated entity
     * @throws UserNotFoundException if user not found by given id
     * @throws OperationFailedException if update failed
     */
    @Transactional
    public UserEntity updatePasswordById(Long id, String newPassword) {
        UserEntity userFromDb = findById(id);
        if (userFromDb == null) {
            throw new UserNotFoundException("User with id " + id +" not found");
        }

        String encodedPassword = encoder.encode(newPassword);

        int affectedRows = userDao.updateUserPasswordById(encodedPassword, id);
        if (affectedRows <= 0) {
            throw new OperationFailedException("Failed to update user data");
        }

        detach(userFromDb);

        return findById(id);
    }

    /**
     * Retrieves an entity from database by given id. Throws exception, if no user found.
     * Updates user age into database.
     * Returns updated entity from database after update committed
     * @param id of user to update
     * @param newAge for update
     * @return updated entity
     * @throws UserNotFoundException if user not found by given id
     * @throws OperationFailedException if update failed
     */
    @Transactional
    public UserEntity updateAgeById(Long id, int newAge) {
        UserEntity userFromDb = findById(id);
        if (userFromDb == null) {
            throw new UserNotFoundException("User with id " + id +" not found");
        }
        detach(userFromDb);

        int affectedRows = userDao.updateUserAgeById(newAge, id);
        if (affectedRows <= 0) {
            throw new OperationFailedException("Failed to update user data");
        }

        return findById(id);
    }

    /**
     * Deletes the given entity from database.
     * @param user entity to be deleted
     */
    @Transactional
    @Override
    public void delete(UserEntity user) {
        userDao.delete(user);
    }

    /**
     * Retrieves an entity from database by given id and throws exception, if no user found.
     * Deletes entity from database.
     * @param id of user to delete
     * @throws UserNotFoundException if user not found by given id
     * @throws OperationFailedException if delete failed
     */
    @Transactional
    public void deleteById(Long id) {
        UserEntity userFromDb = findById(id);
        if (userFromDb == null) {
            throw new UserNotFoundException("User with id " + id +" not found");
        }
        detach(userFromDb);

        int affectedRows = userDao.deleteUserById(id);
        if (affectedRows <= 0) {
            throw new OperationFailedException("Failed to delete user data");
        }
    }

    /**
     * Deletes all entities from database.
     */
    @Transactional
    @Override
    public void deleteAll() {
        userDao.deleteAllUsers();
    }

    /**
     * Sets values from fields of updateData object into updatable fields only
     * of the given entity from database
     * @param userToUpdate entity from database to update
     * @param updateData object containing update information
     * @return given entity updated with values from updateData object
     */
    @Override
    public UserEntity updateDomainWithNewData(UserEntity userToUpdate, UserEntity updateData) {
        userToUpdate.setUserName(updateData.getUserName());
        userToUpdate.setFullName(updateData.getFullName());
        userToUpdate.setPassword(updateData.getPassword());
        userToUpdate.setAge(updateData.getAge());

        return userToUpdate;
    }

    /**
     * Retrieves an entity from database by given user name
     * and throws exception, if no user found.
     * @param userName to check in the database
     * @param rawPassword not encoded password
     * @return true if given raw password after encoding with password encoder
     * matches the encoded password of the entity retrieved from database
     * @throws UserNotFoundException if user not found by given user name
     * @throws PasswordNotMatchException if MAX_TRY attempts count exceeded
     */
    public boolean checkPasswordByUserName(String userName, String rawPassword) {
        UserEntity userFomDb = findByUserName(userName);
        if (userFomDb == null) {
            throw new UserNotFoundException("User with user name " + userName + " not found");
        }

        return encoder.matches(rawPassword, userFomDb.getPassword());
    }

    /**
     * Used to detach from persistent context the entity retrieved from database
     * @param user persistent entity
     */
    public void detach(UserEntity user) {
        userDao.detach(user);
    }

}
