package com.chalova.irina.myfinances.users_service.dao;

import com.chalova.irina.myfinances.users_service.DbUtil;
import com.chalova.irina.myfinances.users_service.entities.Role;
import org.keycloak.component.ComponentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleRepository {
    private static final Logger logger = LoggerFactory.getLogger(RoleRepository.class);

    public RoleRepository() {
    }

    public Role findById(Long id, ComponentModel model) throws SQLException {
        try(Connection connection = DbUtil.getConnection(model);
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM roles WHERE id=?"
            )) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            Role role = null;
            if (rs.next()) {
                role = getRoleFromResult(rs, model);
            }

            return role;
        }
    }

    public Role findByName(String name, ComponentModel model) throws SQLException {
        try(Connection connection = DbUtil.getConnection(model);
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM roles WHERE name=?"
            )) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            Role role = null;
            if (rs.next()) {
                role = getRoleFromResult(rs, model);
            }

            return role;
        }
    }

    /**
     * Returns list of roles
     * @return list of roles or empty list if no any
     * @throws SQLException
     */
    public List<Role> findAll(ComponentModel model) throws SQLException {
        try (Connection connection = DbUtil.getConnection(model);
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM roles"
             )) {

            ResultSet rs = ps.executeQuery();
            List<Role> roles = new ArrayList<>();
            while (rs.next()) {
                Role type = getRoleFromResult(rs, model);
                roles.add(type);
            }

            return roles;
        }
    }

    /**
     * Returns inserted role or null if role already exists
     * @return inserted role if success
     */
    public Role insert(Role role, ComponentModel model) throws SQLException {
        try(Connection connection = DbUtil.getConnection(model);
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
    public Role update(Role role, ComponentModel model) throws SQLException {
        try(Connection connection = DbUtil.getConnection(model);
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM roles WHERE id=?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
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
    public boolean delete(Role role, ComponentModel model) throws SQLException {
        try(Connection connection = DbUtil.getConnection(model);
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

    private Role getRoleFromResult(ResultSet result, ComponentModel model) throws SQLException {
        Role role = new Role();
        logger.info("role found from db: " + result.getString("name"));
        role.setId(result.getLong("id"));
        role.setName(result.getString("name"));

        return role;
    }
}
