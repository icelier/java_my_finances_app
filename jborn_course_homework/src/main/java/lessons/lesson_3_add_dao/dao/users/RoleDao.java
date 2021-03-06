package lessons.lesson_3_add_dao.dao.users;

import lessons.lesson_3_add_dao.datasource.DataFactory;
import lessons.lesson_3_add_dao.entities.users.Role;
import lessons.lesson_3_add_dao.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDao implements Dao<Role,  Long> {
    private static final Logger logger = LoggerFactory.getLogger(RoleDao.class);
    private static RoleDao roleDao;

    public static RoleDao getRoleDao() {
        if (roleDao == null) {
            roleDao = new RoleDao();
        }
        return roleDao;
    }

    private  RoleDao() {}

    @Override
    public Role findById(Long id) throws SQLException {
        try(Connection connection = DataFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM roles WHERE id=?"
            )) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            Role role = null;
            if (rs.next()) {
                role = getRoleFromResult(rs);
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
        try (Connection connection = DataFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM roles"
             )) {

            ResultSet rs = ps.executeQuery();
            List<Role> roles = new ArrayList<>();
            while (rs.next()) {
                Role type = getRoleFromResult(rs);
                roles.add(type);
            }

            return roles;
        }
    }

    /**
     * Returns inserted role or null if role already exists
     * @return inserted role if success
     */
    @Override
    public Role insert(Role role) throws SQLException {
        try(Connection connection = DataFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO roles (name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
            ps.setString(1, role.getName());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to insert new role");
            }
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                role.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("Failed to insert new role, no ID obtained");
            }

            return role;
        }
    }

    /**
     * Returns updated role or null if role does not exist
     * @return updated role if success
     */
    @Override
    public Role update(Role role) throws SQLException {
        try(Connection connection = DataFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM roles WHERE id=?"
            )) {

            ps.setLong(1, role.getId());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return  null;
            } else {
                rs.updateString("name", role.getName());
                rs.updateRow();
            }

            return role;
        }
    }

    /**
     * Returns true if role deleted else false
     * @return true if role deleted
     */
    @Override
    public boolean delete(Role role) throws SQLException {
        try(Connection connection = DataFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM roles WHERE id=?"
            )) {

            ps.setLong(1, role.getId());

            int deletedRowsCount = ps.executeUpdate();
            if (deletedRowsCount <= 0) {
                return false;
            }

            return true;
        }
    }

    private Role getRoleFromResult(ResultSet result) throws SQLException {
        Role role = new Role();
        logger.debug("role found from db: " + result.getString("name"));
        role.setId(result.getLong("id"));
        role.setName(result.getString("name"));

        return role;
    }
}
