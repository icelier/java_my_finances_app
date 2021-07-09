package lessons.lesson_4.dao.users;

import lessons.lesson_4.entities.users.Role;
import lessons.lesson_4.entities.users.UserEntity;
import lessons.lesson_4.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class UserDao implements Dao<UserEntity, Long> {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    private final DataSource dataSource;

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UserEntity findById(Long id) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM users WHERE id=?"
            )) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            UserEntity user = null;
            if (rs.next()) {
                user = getUserFromResult(rs, id);
            }

            return user;
        }
    }

    @Override
    public List<UserEntity> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM users"
             )) {

            ResultSet rs = ps.executeQuery();
            List<UserEntity> users = new ArrayList<>();
            while (rs.next()) {
                UserEntity user = getUserFromResult(rs, rs.getLong("id"));
                users.add(user);
            }

            return users;
        }
    }

    /**
     * Returns inserted user or null if user already exists
     * @return inserted user if success
     */
    @Override
    public UserEntity insert(UserEntity user) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (username, password, fullname, age, email) VALUES (?, ?, ?, ?,  ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
            UserEntity insertedUser = insertUser(user, ps);
            if (insertedUser == null) {
                throw new SQLException("Failed to insert new user");
            }

            if (!insertIntoUsersRoles(insertedUser.getId(), 1L)) {
                throw new SQLException("Failed to add ROLE_USER for user");
            }
            List<Role> userRoles = new ArrayList<Role>();
            userRoles.add(new Role(1L, "ROLE_USER"));
            insertedUser.setRoles(userRoles);

            return insertedUser;
        }
    }

    private static UserEntity insertUser(UserEntity user, PreparedStatement ps) throws SQLException {
        ps.setString(1, user.getUserName());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getFullName());
        ps.setInt(4, user.getAge());
        ps.setString(5, user.getEmail());

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

    private boolean insertIntoUsersRoles(Long userId, Long roleId) throws SQLException {
        try(Connection connection = dataSource.getConnection();
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
     * Returns updated user or null if user does not exist
     * @return updated user if success
     */
    @Override
    public UserEntity update(UserEntity user) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM users INNER JOIN users_roles " +
                    "ON users.id = users_roles.user_id WHERE users.id=?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            )) {

            ps.setLong(1, user.getId());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return  null;
            } else {
                rs.updateString("username", user.getUserName());
                rs.updateString("fullname", user.getFullName());
                rs.updateString("email", user.getEmail());
                rs.updateString("password", user.getPassword());
                rs.updateInt("age", user.getAge());
                rs.updateRow();

                updateUserRoles(user);
            }

            return user;
        }
    }

    private void updateUserRoles(UserEntity user) throws SQLException {
        List<Role> currentRoles = getUserRoles(user.getId());
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
            deleteUserRoles(currentRoles, user.getId());
        }

        if (!updatedRoles.isEmpty()) {
            addNewUserRoles(updatedRoles, user.getId());
        }
    }

    private void deleteUserRoles(Collection<Role> roles, Long userId) throws SQLException {
        for (Role role:
             roles) {
            if (!deleteRoleFromUsersRoles(role, userId)) {
                throw new SQLException("Failed to delete user role");
            }
        }
    }

    private boolean deleteRoleFromUsersRoles(Role role, Long userId) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM users_roles WHERE user_id=? AND role_id=?")
        ) {
            ps.setLong(1, userId);
            ps.setLong(2, role.getId());

            int affectedRows = ps.executeUpdate();

            return affectedRows == 0;
        }
    }

    private void addNewUserRoles(Collection<Role> roles, Long userId) throws SQLException {
        for (Role role:
                roles) {
            if (!insertIntoUsersRoles(role.getId(), userId)) {
                throw new SQLException("Failed to add user role");
            }
        }
    }

    /**
     * Returns true if user deleted else false
     * @return true if user deleted
     */
    @Override
    public boolean delete(UserEntity user) throws SQLException {
        if (!deleteUser(user)) {
            return false;
        }

        deleteUserRoles(user.getRoles(), user.getId());

        return true;
    }


    private boolean deleteUser(UserEntity user) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM users WHERE id=?"
            )) {

            ps.setLong(1, user.getId());

            int deletedRowsCount = ps.executeUpdate();

            return deletedRowsCount > 0;
        }
    }

    private UserEntity getUserFromResult(ResultSet rs, Long id) throws SQLException {
        UserEntity user = new UserEntity();
        logger.debug("User found from db: " + rs.getString("username"));
        user.setId(id);
        user.setUserName(rs.getString("username"));
        user.setFullName(rs.getString("fullname"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setAge(rs.getInt("age"));
        List<Role> roles = getUserRoles(rs.getLong("user_id"));
        user.setRoles(roles);

        return user;
    }

    /**
     * returns roles for the given user id from mayToMany related table
     * @param userId
     * @return list of roles for the given user id or empty list if no any
     * @throws SQLException
     */
    private List<Role> getUserRoles(Long userId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT roles.id, roles.name FROM users_roles INNER JOIN roles" +
                             "ON users_roles.role_id=roles.id WHERE user_id=?;"
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
}
