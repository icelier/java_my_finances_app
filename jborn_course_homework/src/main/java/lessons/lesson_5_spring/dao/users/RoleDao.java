package lessons.lesson_5_spring.dao.users;

import lessons.lesson_5_spring.dao.AbstractDao;
import lessons.lesson_5_spring.entities.users.Role;
import lessons.lesson_5_spring.exceptions.already_exists_exception.RoleAlreadyExistsException;
import lessons.lesson_5_spring.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_5_spring.exceptions.not_found_exception.RoleNotFoundException;
import lessons.lesson_5_spring.exceptions.operation_failed.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class RoleDao extends AbstractDao<Role,  Long> {
    private static final Logger logger = LoggerFactory.getLogger(RoleDao.class);

    private final DataSource dataSource;

    public RoleDao(DataSource dataSource) {
        super(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public Role findById(Long id) throws SQLException {
        return executeFindByIdQuery(id);
    }

    /**
     * Returns role found in database by its name or null of no any
     * @param name of the role
     * @return role or null
     * @throws SQLException if database access error occurred, if query is oncorrect
     */

    public Role findByName(String name) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    getFindByNameQuery()
            )) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            Role role = null;
            if (rs.next()) {
                role = getDomainFromQueryResult(rs);
            }

            return role;
        }
    }

    /**
     * Returns list of roles
     * @return list of roles or empty list if no any
     * @throws SQLException
     */
    @Override
    public List<Role> findAll() throws SQLException {
        return executeFindAllQuery();
    }

    /**
     * Returns inserted role
     * @return inserted role if success
     */
    @Override
    public Role insert(Role role) throws SQLException, OperationFailedException, RoleAlreadyExistsException {
        boolean alreadyExists = checkIfAlreadyExists(role);
        if (alreadyExists) {
            throw new RoleAlreadyExistsException("Role already exists");
        }
        return executeInsertQuery(role);
    }

    /**
     * Returns updated role
     * @return updated role if success
     */
    @Override
    public Role update(Role role) throws SQLException, RoleNotFoundException, OperationFailedException {
        try {
            executeUpdateQuery(role.getId(), role);
        } catch (DataNotFoundException e) {
            throw new RoleNotFoundException("Role " + role.getName() + " not found in the database");
        }

        return role;
    }

    @Override
    public void delete(Role role) throws SQLException, OperationFailedException {
        executeDeleteQuery(role);
    }


    @Override
    protected String getInsertQuery() {
        return "INSERT INTO roles (name) VALUES (?)";
    }

    @Override
    protected String getFindByIdQuery() {
        return "SELECT * FROM roles WHERE id=?";
    }

    private String getFindByNameQuery() {
        return "SELECT * FROM roles WHERE name=?";
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT * FROM roles ORDER BY id ASC";
    }

    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM roles WHERE id=?";
    }

    @Override
    protected String getFindDomainQuery() {
        return "SELECT * FROM roles WHERE name=?";
    }

    @Override
    public void setupFindByIdQuery(PreparedStatement ps, Long id) throws SQLException {
        ps.setLong(1, id);
    }

    @Override
    public void setupInsertQuery(PreparedStatement ps, Role role) throws SQLException {
        ps.setString(1, role.getName());
    }

    @Override
    public void setupDeleteQuery(PreparedStatement ps, Role role) throws SQLException {
        ps.setLong(1, role.getId());
    }

    @Override
    public void setupFindDomainQuery(PreparedStatement ps, Role role) throws SQLException {
        ps.setString(1, role.getName());
    }

    @Override
    public Role getDomainFromQueryResult(ResultSet rs) throws SQLException {
        Role role = new Role();
        logger.debug("role found from db: " + rs.getString("name"));
        role.setId(rs.getLong("id"));
        role.setName(rs.getString("name"));

        return role;
    }

    @Override
    public void updateDomain(ResultSet rs, Role role) throws SQLException {
        rs.updateString("name", role.getName());
        rs.updateRow();
    }
}
