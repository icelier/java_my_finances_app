package lessons.lesson_8_hibernate.dao.users;

import lessons.lesson_8_hibernate.dao.AbstractDao;
import lessons.lesson_8_hibernate.dao.users.RoleDao;
import lessons.lesson_8_hibernate.entities.users.Role;
import lessons.lesson_8_hibernate.entities.users.UserEntity;
import lessons.lesson_8_hibernate.entities.users.UserProjection;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.RoleNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Repository
public class UserDao extends AbstractDao<UserEntity, Long> {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    private final DataSource dataSource;
    private final RoleDao roleDao;

    public UserDao(DataSource dataSource, RoleDao roleDao) {
        super(dataSource);
        this.dataSource = dataSource;
        this.roleDao = roleDao;
    }

    /**
     * Returns user entity found by given id in database or null if no any
     * with roles obtained in a separate query
     * @param id for search user in the database
     * @return user entity or null
     * @throws SQLException if database access error occurred, if query is incorrect
     */
    @Override
    public UserEntity findById(Long id) throws SQLException {
        return executeFindByIdQuery(id);
    }

    /**
     * Returns user entity found by given username in database or null if no any
     * with roles obtained in a separate query
     * @param userName for search user in the database
     * @return user entity or null
     * @throws SQLException if database access error occurred, if query is incorrect
     */
    public UserEntity findByUserName(String userName) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    getFindByUserNameQuery()
            )) {

            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            UserEntity user = null;
            if (rs.next()) {
                user = getDomainFromQueryResult(rs);
            }

            return user;
        }
    }

    public UserEntity findByEmail(String email) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    getFindByEmailQuery()
            )) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            UserEntity user = null;
            if (rs.next()) {
                user = getDomainFromQueryResult(rs);
            }

            return user;
        }
    }

    /**
     * Returns list of user entities found in database or empty list if no any
     * with roles obtained in a separate query
     * @return list of user entities or empty list
     * @throws SQLException if database access error occurred, if query is incorrect
     */
    @Override
    public List<UserEntity> findAll() throws SQLException {
        return executeFindAllQuery();
    }

    /**
     * Returns inserted user entity with new id generated. First checks for entity presence in the database
     * based on equals method criteria
     * Updates related table users_roles with many-to-many relation in the same transaction. If transaction fails at any stage,
     * makes transaction rollback
     * @param user user entity to get parameters for insert query
     * @return inserted user with generated id
     * @throws SQLException if database access error occurred, if query parameter is incorrect
     * @throws OperationFailedException if id generation for inserted user failed,
     * if insertion of ROLE_USER into related table users_roles for given user failed
     * @throws UserAlreadyExistsException if user found in the database by equals method criteria
     * @throws RoleNotFoundException if ROLE_USER not found in the database
     */
    @Override
    public UserEntity insert(UserEntity user) throws SQLException, OperationFailedException, RoleNotFoundException, UserAlreadyExistsException {
        boolean alreadyExists = checkIfAlreadyExists(user);
        if (alreadyExists) {
            throw new UserAlreadyExistsException("User already exists");
        }
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(
                    getInsertQuery(),
                    Statement.RETURN_GENERATED_KEYS);

            setupInsertQuery(ps, user);
            executeDataManagingQuery(ps);

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setEntityId(generatedKeys.getLong(1));
            } else {
                throw new OperationFailedException("Failed to insert new user, no ID obtained");
            }

            Role roleUser = roleDao.findByName("ROLE_USER");
            if (roleUser == null) {
                throw new RoleNotFoundException("ROLE_USER not found in the database");
            }
            try {
                insertIntoUsersRoles(connection, user.getId(), roleUser.getId());
            } catch (SQLException e) {
                throw new OperationFailedException("Failed to add ROLE_USER for new user");
            }

            List<Role> userRoles = new ArrayList<>();
            userRoles.add(roleUser);
            user.setRoles(userRoles);

            return user;
        } catch (SQLException e) {
            catchTransactionalException(connection, e);
        } finally {
            processTransactionalFinallyBlock(connection, ps);
        }

        return user;
//        user = executeInsertQuery(user);
    }

    /**
     * Inserts ROLE_USER for newly inserted user if user entity insertion into table users did not fail.
     * Commits insertion in the same transaction as user insertion, if fails, whole transaction gets rollbacked.
     * Checks for presence of row in table users_roles with the same userId and roleId,
     * if found, throws OperationFailedException
     * @param connection in which user entity insertion performed
     * @param userId id generated for newly inserted user
     * @param roleId id of ROLE_USER
     * @throws SQLException if database access error occurred, if query parameter is incorrect
     * @throws OperationFailedException if insertion of ROLE_USER for given user failed,
     * if ROLE_USER for given user already exists in the database
     */
    private void insertIntoUsersRoles(Connection connection, Long userId, Long roleId) throws SQLException, OperationFailedException {
        List<Role> roles = getUserRoles(connection, userId);
        if (!roles.isEmpty()) {
            throw new OperationFailedException("User id with such role already exists");
        }
        try(PreparedStatement ps = connection.prepareStatement(
                    getInsertIntoUsersRolesQuery()
            )) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new OperationFailedException("Failed to insert user roles into database");
            }
        }
    }

     public List<Role> getUserRoles(Long userId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     getFindRolesByUserIdQuery()
             )) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Role> roles = new ArrayList<>();
            while (rs.next()) {
                Role role = roleDao.getDomainFromQueryResult(rs);
                roles.add(role);
            }

            return roles;
        }
    }

    /**
     * Returns roles for the given user id from manyToMany related table users_roles
     * @param userId to search for roles
     * @return list of roles for the given user id or empty list if no any
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     */
    private List<Role> getUserRoles(Connection connection, Long userId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                getFindRolesByUserIdQuery()
        )) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Role> roles = new ArrayList<>();
            while (rs.next()) {
                Role role = roleDao.getDomainFromQueryResult(rs);
                roles.add(role);
            }

            return roles;
        }
    }


    /**
     * Returns updated user
     * Updates related table users_roles with many-to-many relation in the same transaction. If transaction fails at any stage,
     * makes transaction rollback
     * @param user entity to be updated in the database
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     * @throws UserNotFoundException if given user not found in the database
     * @throws OperationFailedException if update of user roles failed  (either deletion of unnecessary roles
     * or insertion of new roles into many-to-many related table users_roles)
     * @return updated user entity if success
     */
    @Override
    public UserEntity update(UserEntity user) throws SQLException, UserNotFoundException, OperationFailedException {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(
                     getFindByIdQuery(),
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE);
            setupFindByIdQuery(ps, user.getId());
            ResultSet result = ps.executeQuery();
            if (!result.next()) {
                throw new UserNotFoundException("User not found");
            }
            updateDomain(result, user);

            updateUserRoles(connection, user);

            return user;
        } catch (SQLException e) {
            catchTransactionalException(connection, e);
        } finally {
            processTransactionalFinallyBlock(connection, ps);
        }
        return user;
    }

    private void updateUserRoles(Connection connection, UserEntity user) throws SQLException, OperationFailedException {
        List<Role> currentRoles = getUserRoles(connection, user.getId());
        logger.debug("Current roles from db: ");
        for (Role role : currentRoles) {
            logger.debug(role.getName());
        }

        Collection<Role> updatedRoles = user.getRoles();
        logger.debug("Updated roles from updated user: ");
        for (Role role : updatedRoles) {
            logger.debug(role.getName());
        }

        Iterator<Role> iter = currentRoles.iterator();
        Role role;
        while(iter.hasNext()) {
            role = iter.next();
            if (updatedRoles.contains(role)) {
                iter.remove();
                updatedRoles.remove(role);
            }
        }

        logger.debug("Current roles from db must be empty: ");
        for (Role curRole : currentRoles) {
            logger.debug(curRole.getName());
        }

        if (!currentRoles.isEmpty()) {
            deleteUserRoles(connection, currentRoles, user.getId());
        }

        if (!updatedRoles.isEmpty()) {
            addNewUserRoles(connection, updatedRoles, user.getId());
        }
    }

    /**
     * Deletes user with specified id
     * Updates related table users_roles with many-to-many relation in the same transaction. If transaction fails at any stage,
     * makes transaction rollback
     * @param user
     * @throws SQLException if database access error occurred, if underlying query is incorrect
     * @throws OperationFailedException if deletion of row in many-to-may related table users_roles failed
     */
    @Override
    public void delete(UserEntity user) throws SQLException, OperationFailedException {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(getDeleteQuery());

            setupDeleteQuery(ps, user);
            executeDataManagingQuery(ps);
        } catch (SQLException e) {
            catchTransactionalException(connection, e);
        } finally {
            processTransactionalFinallyBlock(connection, ps);
        }
    }

    private void deleteUserRoles(Connection connection, Collection<Role> roles, Long userId) throws SQLException, OperationFailedException {
        for (Role role:
             roles) {
            deleteRoleFromUsersRoles(connection, userId, role.getId());
        }
    }

    private void deleteRoleFromUsersRoles(Connection connection, Long userId, Long roleId) throws SQLException, OperationFailedException {
        try(PreparedStatement ps = connection.prepareStatement(
                    getDeleteFromUsersRolesQuery())) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new OperationFailedException("Failed to delete user roles");
            }
        }
    }

    private void addNewUserRoles(Connection connection, Collection<Role> roles, Long userId) throws SQLException, OperationFailedException {
        for (Role role:
                roles) {
            insertIntoUsersRoles(connection, userId, role.getId());
        }
    }

    private void catchTransactionalException(Connection connection, Exception e) throws SQLException {
        if (connection != null) {
            try {
                connection.rollback();
                e.printStackTrace();
                throw new SQLException("Failed to commit transaction");
            } catch (SQLException throwables) {
                throw new SQLException("Failed to commit transaction");
            }
        }
    }

    private void processTransactionalFinallyBlock(Connection connection, PreparedStatement ps) throws SQLException {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                throw new SQLException("Failed to close connection");
            }
        }
        if (connection != null) {
            connection.setAutoCommit(true);
            try {
                connection.close();
            } catch (SQLException e) {
                throw new SQLException("Failed to close connection");
            }
        }
    }

    private UserProjection getUserProjectionFromResult(ResultSet rs) throws SQLException {
        UserProjection user = new UserProjection();
        logger.debug("User found from db: " + rs.getString("username"));
        user.setId(rs.getLong("id"));
        user.setUserName(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        List<Role> roles = getUserRoles(rs.getLong("user_id"));
        user.setRoles(roles);

        return user;
    }

    @Override
    protected String getInsertQuery() {
        return "INSERT INTO users (username, password, fullname, age, email) VALUES (?, ?, ?, ?,  ?)";
    }

    private String getInsertIntoUsersRolesQuery() {
        return "INSERT INTO users_roles (user_id, role_id) VALUES (?, ?)";
    }

    @Override
    protected String getFindByIdQuery() {
        return "SELECT * FROM users WHERE id=?";
    }

    private String getFindByUserNameQuery() {
        return "SELECT * FROM users WHERE username=?";
    }

    private String getFindByEmailQuery() {
        return "SELECT * FROM users WHERE email=?";
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT * FROM users ORDER BY id ASC";
    }

    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM users WHERE id=?";
    }

    private String getDeleteFromUsersRolesQuery() {
        return "DELETE FROM users_roles WHERE user_id=? AND role_id=?";
    }

    private String getFindRolesByUserIdQuery() {
        return "SELECT r.* " +
                "FROM roles AS r " +
                "INNER JOIN users_roles AS ur " +
                "ON r.id=ur.role_id " +
                "WHERE ur.user_id=?";
    }

    @Override
    protected String getFindDomainQuery() {
        return "SELECT * FROM users WHERE email=?";
    }

    @Override
    public void setupFindByIdQuery(PreparedStatement ps, Long id) throws SQLException {
        ps.setLong(1, id);
    }

    @Override
    public void setupInsertQuery(PreparedStatement ps, UserEntity userEntity) throws SQLException {
        ps.setString(1, userEntity.getUserName());
        ps.setString(2, userEntity.getPassword());
        ps.setString(3, userEntity.getFullName());
        ps.setInt(4, userEntity.getAge());
        ps.setString(5, userEntity.getEmail());
    }

    @Override
    public void setupDeleteQuery(PreparedStatement ps, UserEntity userEntity) throws SQLException {
        ps.setLong(1, userEntity.getId());
    }

    @Override
    public void setupFindDomainQuery(PreparedStatement ps, UserEntity userEntity) throws SQLException {
        ps.setString(1, userEntity.getEmail());
    }

    @Override
    public UserEntity getDomainFromQueryResult(ResultSet rs) throws SQLException {
        UserEntity user = new UserEntity();
        logger.debug("User found from db: " + rs.getString("username"));
        user.setId(rs.getLong("id"));
        user.setUserName(rs.getString("username"));
        user.setFullName(rs.getString("fullname"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setAge(rs.getInt("age"));
        List<Role> roles = getUserRoles(rs.getLong("id"));
        user.setRoles(roles);

        return user;
    }

    @Override
    public void updateDomain(ResultSet rs, UserEntity user) throws SQLException {
        rs.updateString("username", user.getUserName());
        rs.updateString("fullname", user.getFullName());
        rs.updateString("email", user.getEmail());
        rs.updateString("password", user.getPassword());
        rs.updateInt("age", user.getAge());
        rs.updateRow();
    }
}
