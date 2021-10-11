package com.chalova.irina.myfinances.users_service.services;

import com.chalova.irina.myfinances.users_service.dao.RoleRepository;
import com.chalova.irina.myfinances.users_service.dao.UserRepository;
import com.chalova.irina.myfinances.users_service.entities.Role;
import com.chalova.irina.myfinances.users_service.entities.UserEntity;
import com.chalova.irina.myfinances.users_service.exceptions.UserAlreadyExistsException;
import com.chalova.irina.myfinances.users_service.exceptions.UserNotFoundException;
import org.keycloak.component.ComponentModel;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;
import java.util.*;

public class UserService
{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        encoder = new BCryptPasswordEncoder();
    }

    public UserEntity findById(Long id, ComponentModel model) {
        UserEntity user = null;
        try {
            user = userRepository.findById(id, model);
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }

        return user;
    }

    public UserEntity findByUserName(String userName, ComponentModel model) {
        UserEntity user = null;
        try {
            user = userRepository.findByUserName(userName, model);
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }

        return user;
    }

    public UserEntity findByEmail(String email, ComponentModel model) {
        UserEntity user = null;
        try {
            user = userRepository.findByEmail(email, model);
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }

        return user;
    }

    public List<UserEntity> findAll(ComponentModel model) {
        List<UserEntity> users = new ArrayList<>();
        try {
            users = userRepository.findAll(model);
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }

        return users;
    }

    public int getUsersCount(ComponentModel model) {
        try {
            return userRepository.getUsersCount(model);
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }
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
    public UserEntity insert(UserEntity user, ComponentModel model) throws UserAlreadyExistsException, SQLException {
        UserEntity userFromDbByEmail = findByEmail(user.getEmail(), model);
        if (userFromDbByEmail != null) {
            throw new UserAlreadyExistsException(
                    "Email " + user.getEmail() + " already registered"
            );
        }

        UserEntity userFomDbByUsername = findByUserName(user.getUserName(), model);
        if (userFomDbByUsername != null) {
            throw new UserAlreadyExistsException(
                    "User by username " + user.getUserName() + "  has already been registered"
            );
        }

        user.setPassword(encoder.encode(user.getPassword()));

        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleRepository.findByName("ROLE_USER", model));
        user.setRoles(userRoles);

        user = userRepository.insert(user, model);

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
    public UserEntity update(Long id, UserEntity user, ComponentModel model) {
        UserEntity userFromDb = findById(id, model);
        if (userFromDb == null) {
            throw new UserNotFoundException("User with id " + id +" not found");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        updateDomainWithNewData(userFromDb, user);

        return findById(id, model);
    }

    /**
     * Retrieves an entity from database by given id. Throws exception, if no user found.
     * Updates userName into database.
     * Returns updated entity from database after update committed
     * @param id of user to update
     * @param newUserName for update
     * @return updated entity
     * @throws UserNotFoundException if user not found by given id
     */
    public UserEntity updateUserNameById(Long id, String newUserName, ComponentModel model) {
        UserEntity userFromDb = findById(id, model);
        if (userFromDb == null) {
            throw new UserNotFoundException("User with id " + id +" not found");
        }

        try {
            userRepository.updateUserNameById(newUserName, id, model);
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }

        return findById(id, model);
    }

    /**
     * Retrieves an entity from database by given id. Throws exception, if no user found.
     * Updates firstName into database.
     * Returns updated entity from database after update committed
     * @param id of user to update
     * @param newFirstName for update
     * @return updated entity
     * @throws UserNotFoundException if user not found by given id
     */
    public UserEntity updateFirstNameById(Long id, String newFirstName, ComponentModel model) {
        UserEntity userFromDb = findById(id, model);
        if (userFromDb == null) {
            throw new UserNotFoundException("User with id " + id +" not found");
        }

        try {
            userRepository.updateUserFirstNameById(newFirstName, id, model);
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }

        return findById(id, model);
    }

    /**
     * Retrieves an entity from database by given id. Throws exception, if no user found.
     * Updates lastName into database.
     * Returns updated entity from database after update committed
     * @param id of user to update
     * @param newLastName for update
     * @return updated entity
     * @throws UserNotFoundException if user not found by given id
     */
    public UserEntity updateLastNameById(Long id, String newLastName, ComponentModel model) {
        UserEntity userFromDb = findById(id, model);
        if (userFromDb == null) {
            throw new UserNotFoundException("User with id " + id +" not found");
        }

        try {
            userRepository.updateUserLastNameById(newLastName, id, model);
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }

        return findById(id, model);
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
     */
    public UserEntity updatePasswordById(Long id, String newPassword, ComponentModel model) {
        UserEntity userFromDb = findById(id, model);
        if (userFromDb == null) {
            throw new UserNotFoundException("User with id " + id +" not found");
        }

        String encodedPassword = encoder.encode(newPassword);

        try {
            userRepository.updateUserPasswordById(encodedPassword, id, model);
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }

        return findById(id, model);
    }

    /**
     * Retrieves an entity from database by given id. Throws exception, if no user found.
     * Updates user age into database.
     * Returns updated entity from database after update committed
     * @param id of user to update
     * @param newAge for update
     * @return updated entity
     * @throws UserNotFoundException if user not found by given id
     */
    public UserEntity updateAgeById(Long id, int newAge, ComponentModel model) {
        UserEntity userFromDb = findById(id, model);
        if (userFromDb == null) {
            throw new UserNotFoundException("User with id " + id +" not found");
        }

        try {
            userRepository.updateUserAgeById(newAge, id, model);
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }

        return findById(id, model);
    }

    /**
     * Deletes the given entity from database.
     * @param user entity to be deleted
     */
//    public void delete(UserEntity user, ComponentModel model) {
//        userRepository.delete(user);
//    }

    /**
     * Deletes all entities from database.
     */
    public void deleteAll(ComponentModel model) {
        try {
        userRepository.deleteAll(model);
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }
    }

    /**
     * Sets values from fields of updateData object into updatable fields only
     * of the given entity from database
     * @param userToUpdate entity from database to update
     * @param updateData object containing update information
     * @return given entity updated with values from updateData object
     */
    public UserEntity updateDomainWithNewData(UserEntity userToUpdate, UserEntity updateData) {
        userToUpdate.setUserName(updateData.getUserName());
        userToUpdate.setFirstName(updateData.getFirstName());
        userToUpdate.setLastName(updateData.getLastName());
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
     */
    public boolean checkPasswordByUserName(String userName, String rawPassword,
                                           ComponentModel model) {
        UserEntity userFomDb = findByUserName(userName, model);
        if (userFomDb == null) {
            throw new UserNotFoundException("User with user name " + userName + " not found");
        }

        return encoder.matches(rawPassword, userFomDb.getPassword());
    }

}
