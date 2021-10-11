package com.chalova.irina.myfinances.users_service.dao;

import com.chalova.irina.myfinances.users_service.DbUtil;
import com.chalova.irina.myfinances.users_service.entities.Role;
import com.chalova.irina.myfinances.users_service.entities.UserEntity;
import com.chalova.irina.myfinances.users_service.exceptions.UserNotFoundException;
import org.keycloak.component.ComponentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public UserRepository() {

    }

    public UserEntity findById(Long id, ComponentModel model) throws SQLException {
        try(Connection connection = DbUtil.getConnection(model);
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM users WHERE id=?"
            )) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            UserEntity user = null;
            if (rs.next()) {
                user = getUserFromResult(rs, model);
            }

            return user;
        }
    }

    public UserEntity findByUserName(String userName, ComponentModel model) throws SQLException {
        try(Connection connection = DbUtil.getConnection(model);
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM users WHERE username=?"
            )) {

            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            UserEntity user = null;
            if (rs.next()) {
                user = getUserFromResult(rs, model);
            }

            return user;
        }
    }

    public UserEntity findByEmail(String email, ComponentModel model)
            throws SQLException {
        try(Connection connection = DbUtil.getConnection(model);
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM users WHERE email=?"
            )) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            UserEntity user = null;
            if (rs.next()) {
                user = getUserFromResult(rs, model);
            }

            return user;
        }
    }

    public List<UserEntity> findAll(ComponentModel model) throws SQLException {
        try (Connection connection = DbUtil.getConnection(model);
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM users"
             )) {

            ResultSet rs = ps.executeQuery();
            List<UserEntity> users = new ArrayList<>();
            while (rs.next()) {
                UserEntity user = getUserFromResult(rs, model);
                users.add(user);
            }

            return users;
        }
    }

    public int getUsersCount(ComponentModel model) throws SQLException {
        try (Connection connection = DbUtil.getConnection(model);
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT count(*) AS total FROM users"
             )) {

            ResultSet rs = ps.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt("total");
            }

            return count;
        }
    }

    /**
     * Returns inserted user or null if user already exists
     * @return inserted user if success
     * @throws SQLException if  a database access error occurs, if insertion failed
     */
    public UserEntity insert(UserEntity user, ComponentModel model) throws SQLException {
        try(Connection connection = DbUtil.getConnection(model);
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (username, password, firstName, lastName, age, email) " +
                            "VALUES (?, ?, ?, ?,  ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
            UserEntity insertedUser = insertUser(user, ps);
            if (insertedUser == null) {
                throw new SQLException("Failed to insert new user");
            }

            if (!insertIntoUsersRoles(insertedUser.getId(), 1L, model)) {
                throw new SQLException("Failed to add ROLE_USER for user");
            }
            List<Role> userRoles = new ArrayList<Role>();
            userRoles.add(new Role(1L, "ROLE_USER"));
            insertedUser.setRoles(userRoles);

            return insertedUser;
        }
    }

    private static UserEntity insertUser(UserEntity user, PreparedStatement ps)
            throws SQLException {
        ps.setString(1, user.getUserName());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getFirstName());
        ps.setString(4, user.getLastName());
        ps.setInt(5, user.getAge());
        ps.setString(6, user.getEmail());

        int affectedRows = ps.executeUpdate();
        if (affectedRows == 0) {
            return null;
        }
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            user.setId(generatedKeys.getLong(1));
        } else {
            throw new SQLException("Failed to insert new user, no ID obtained");
        }

        return user;
    }

    private boolean insertIntoUsersRoles(Long userId, Long roleId, ComponentModel model) throws SQLException {
        try(Connection connection = DbUtil.getConnection(model);
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users_roles (user_id, role_id) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);

            int affectedRows = ps.executeUpdate();
            return affectedRows != 0;
        }
    }

    /**
     * Updates user's updatable fields
     */
    public void update(UserEntity user, ComponentModel model) throws SQLException {
        try(Connection connection = DbUtil.getConnection(model);
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM users WHERE id=?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            )) {

            ps.setLong(1, user.getId());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new UserNotFoundException();
            } else {
                rs.updateString("username", user.getUserName());
                rs.updateString("firstName", user.getFirstName());
                rs.updateString("lastName", user.getLastName());
                rs.updateString("password", user.getPassword());
                rs.updateInt("age", user.getAge());
                rs.updateRow();

                updateUserRoles(user, model);
            }
        }
    }

    private void updateUserRoles(UserEntity user, ComponentModel model) throws SQLException {
        List<Role> currentRoles = getUserRoles(user.getId(), model);
        Collection<Role> updatedRoles = user.getRoles();
        Iterator<Role> iter = currentRoles.iterator();
        Role role = null;
        while(iter.hasNext()) {
            role = iter.next();
            if (updatedRoles.contains(role)) {
                iter.remove();
                updatedRoles.remove(role);
            }
        }

        if (!currentRoles.isEmpty()) {
            deleteUserRoles(currentRoles, user.getId(), model);
        }

        if (!updatedRoles.isEmpty()) {
            addNewUserRoles(updatedRoles, user.getId(), model);
        }
    }

    private void deleteUserRoles(Collection<Role> roles, Long userId, ComponentModel model)
            throws SQLException {
        for (Role role:
                roles) {
            if (!deleteRoleFromUsersRoles(role, userId, model)) {
                throw new SQLException("Failed to delete user role");
            }
        }
    }

    private boolean deleteRoleFromUsersRoles(Role role, Long userId, ComponentModel model)
            throws SQLException {
        try(Connection connection = DbUtil.getConnection(model);
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM users_roles WHERE user_id=? AND role_id=?")
        ) {
            ps.setLong(1, userId);
            ps.setLong(2, role.getId());

            int affectedRows = ps.executeUpdate();

            return affectedRows == 0;
        }
    }

    private void addNewUserRoles(Collection<Role> roles, Long userId, ComponentModel model)
            throws SQLException {
        for (Role role:
                roles) {
            if (!insertIntoUsersRoles(role.getId(), userId, model)) {
                throw new SQLException("Failed to add user role");
            }
        }
    }

    /**
     * Returns true if user deleted else false
     * @return true if user deleted
     */
    public boolean delete(UserEntity user, ComponentModel model)
            throws SQLException {
        if (!deleteUser(user, model)) {
            return false;
        }

        deleteUserRoles(user.getRoles(), user.getId(), model);

        return true;
    }

    /**
     * Delete all users
     */
    public void deleteAll(ComponentModel model)
            throws SQLException {
        List<UserEntity> users = findAll(model);
        for (UserEntity user: users) {
            deleteUser(user, model);
        }
    }


    private boolean deleteUser(UserEntity user, ComponentModel model)
            throws SQLException {
        try(Connection connection = DbUtil.getConnection(model);
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM users WHERE id=?"
            )) {

            ps.setLong(1, user.getId());

            int deletedRowsCount = ps.executeUpdate();

            return deletedRowsCount > 0;
        }
    }

    private UserEntity getUserFromResult(ResultSet rs, ComponentModel model) throws SQLException {
        UserEntity user = new UserEntity();
        logger.info("User found from db: " + rs.getString("username"));
        user.setId(rs.getLong("id"));
        user.setUserName(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("firstName"));
        user.setLastName(rs.getString("lastName"));
        user.setAge(rs.getInt("age"));
        user.setEmail(rs.getString("email"));
        List<Role> roles = getUserRoles(rs.getLong("id"), model);
        user.setRoles(roles);

        return user;
    }

    /**
     * returns roles for the given user id from mayToMany related table
     * @param userId
     * @return list of roles for the given user id or empty list if no any
     * @throws SQLException
     */
    private List<Role> getUserRoles(Long userId, ComponentModel model)
            throws SQLException {
        try (Connection connection = DbUtil.getConnection(model);
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT r.* " +
                             "FROM roles AS r " +
                             "INNER JOIN users_roles AS ur " +
                             "ON r.id=ur.role_id " +
                             "WHERE ur.user_id=?"
             )) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Role> roles = new ArrayList<>();
            while (rs.next()) {
                Role role = new Role();
                role.setId( rs.getLong("id"));
                role.setName( rs.getString("name"));
                roles.add(role);
            }

            return roles;
        }
    }

    public void updateUserPasswordById(String encodedPassword, Long id, ComponentModel model) throws SQLException {
        try (Connection connection = DbUtil.getConnection(model);
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM users WHERE id=?",
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE
             )) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new UserNotFoundException();
            } else {
                rs.updateString("password", encodedPassword);
                rs.updateRow();
            }
        }
    }

    public void updateUserAgeById(int newAge, Long id, ComponentModel model) throws SQLException {
        try (Connection connection = DbUtil.getConnection(model);
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM users WHERE id=?",
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE
             )) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new UserNotFoundException();
            } else {
                rs.updateInt("age", newAge);
                rs.updateRow();
            }
        }
    }

    public void updateUserFirstNameById(String newFirstName, Long id, ComponentModel model) throws SQLException {
        try (Connection connection = DbUtil.getConnection(model);
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM users WHERE id=?",
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE
             )) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new UserNotFoundException();
            } else {
                rs.updateString("firstname", newFirstName);
                rs.updateRow();
            }
        }
    }

    public void updateUserLastNameById(String newLastName, Long id, ComponentModel model) throws SQLException {
        try (Connection connection = DbUtil.getConnection(model);
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM users WHERE id=?",
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE
             )) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new UserNotFoundException();
            } else {
                rs.updateString("lastname", newLastName);
                rs.updateRow();
            }
        }
    }

    public void updateUserNameById(String newUserName, Long id, ComponentModel model) throws SQLException {
        try (Connection connection = DbUtil.getConnection(model);
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM users WHERE id=?",
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE
             )) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new UserNotFoundException();
            } else {
                rs.updateString("username", newUserName);
                rs.updateRow();
            }
        }
    }
}
