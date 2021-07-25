package lessons.lesson_5_spring.dao;

import lessons.lesson_5_spring.dao.query_converters.QueryConverter;
import lessons.lesson_5_spring.entities.DatabaseEntity;
import lessons.lesson_5_spring.entities.finances.Account;
import lessons.lesson_5_spring.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_5_spring.exceptions.operation_failed.OperationFailedException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<DOMAIN extends DatabaseEntity, ID> implements Dao<DOMAIN, ID>, QueryConverter<DOMAIN, ID> {
    private final DataSource dataSource;

    protected AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected PreparedStatement prepareStatement(String query) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(query);
        ps = setupQuery(ps);

        return ps;
    }

    protected abstract String getInsertQuery();
    protected abstract String getFindByIdQuery();
    protected abstract String getFindAllQuery();
    protected abstract String getDeleteQuery();
    protected String getUpdateQuery() {
        return null;
    }

    protected DOMAIN executeQueryWithSingleResult(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        DOMAIN domain = null;
        if (rs.next()) {
            domain = getDomainFromQueryResult(rs);
        }

        return domain;
    }

    protected List<DOMAIN> executeQueryWithMultipleResult(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();

        List<DOMAIN> domains = new ArrayList<>();
        DOMAIN currentDomain = null;
        while (rs.next()) {
            currentDomain = getDomainFromQueryResult(rs);
            if (currentDomain == null) {
                throw new SQLException("Object construction failed");
            }
            domains.add(currentDomain);
        }

        return domains;
    }

    protected DOMAIN executeFindByIdQuery(ID id) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(getFindByIdQuery())) {
            setupFindByIdQuery(ps, id);

            return executeQueryWithSingleResult(ps);
        }
    }

    protected DOMAIN executeFindByIdQuery(Connection connection, ID id) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement(getFindByIdQuery())) {
            setupFindByIdQuery(ps, id);

            return executeQueryWithSingleResult(ps);
        }
    }

    protected List<DOMAIN> executeFindAllQuery() throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(getFindAllQuery())) {

            return executeQueryWithMultipleResult(ps);
        }
    }

    protected List<DOMAIN> executeFindAllQuery(Connection connection) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement(getFindAllQuery())) {

            return executeQueryWithMultipleResult(ps);
        }
    }


    protected DOMAIN executeUpdateQuery(ID id, DOMAIN domain) throws SQLException, DataNotFoundException, OperationFailedException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(
                getFindByIdQuery(),
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE
        )) {
            setupFindByIdQuery(ps, id);
            ResultSet result = ps.executeQuery();
            if (!result.next()) {
                throw new DataNotFoundException("No matches in the database");
            }
            updateDomain(result, domain);
            return domain;
        }
    }

    protected DOMAIN executeUpdateQuery(Connection connection, ID id, DOMAIN domain) throws SQLException, DataNotFoundException, OperationFailedException {
        try (PreparedStatement ps = connection.prepareStatement(
                     getFindByIdQuery(),
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE
             )) {
            setupFindByIdQuery(ps, id);
            ResultSet result = ps.executeQuery();
            if (!result.next()) {
                throw new DataNotFoundException("No matches in the database");
            }
            updateDomain(result, domain);
            return domain;
        }
    }

    protected void executeDeleteQuery(DOMAIN domain) throws SQLException, OperationFailedException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(getDeleteQuery())) {
            setupDeleteQuery(ps, domain);

            executeDataManagingQuery(ps);
        }
    }

    protected void executeDeleteQuery(Connection connection, DOMAIN domain) throws SQLException, OperationFailedException {
        try (PreparedStatement ps = connection.prepareStatement(getDeleteQuery())) {
            setupDeleteQuery(ps, domain);

            executeDataManagingQuery(ps);
        }
    }


    protected DOMAIN executeInsertQuery(DOMAIN domain) throws SQLException, OperationFailedException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     getInsertQuery(),
                     Statement.RETURN_GENERATED_KEYS
             )) {
            setupInsertQuery(ps, domain);
            executeDataManagingQuery(ps);

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                domain.setEntityId(generatedKeys.getLong(1));
                return domain;
            } else {
                throw new OperationFailedException("Failed to insert new data, no ID obtained");
            }
        }
    }

    protected DOMAIN executeInsertQuery(Connection connection, DOMAIN domain) throws SQLException, OperationFailedException {
        try (PreparedStatement ps = connection.prepareStatement(
                     getInsertQuery(),
                     Statement.RETURN_GENERATED_KEYS
             )) {
            setupInsertQuery(ps, domain);
            executeDataManagingQuery(ps);

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                domain.setEntityId(generatedKeys.getLong(1));
                return domain;
            } else {
                throw new OperationFailedException("Failed to insert new data, no ID obtained");
            }
        }
    }

    protected void executeDataManagingQuery(PreparedStatement ps) throws SQLException, OperationFailedException {
        int affectedRows = ps.executeUpdate();
        if (affectedRows == 0) {
            throw new OperationFailedException("Failed to update data");
        }
    }

    protected boolean checkIfAlreadyExists(DOMAIN domain) throws SQLException {
        if (findDomain(domain) != null) {
            return true;
        }
        return false;
    }

    protected DOMAIN findDomain(DOMAIN domain) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     getFindDomainQuery()
             )) {
            setupFindDomainQuery(ps, domain);
            ResultSet rs = ps.executeQuery();
            DOMAIN domainFromDb = null;
            if (rs.next()) {
                domainFromDb = getDomainFromQueryResult(rs);
            }

            return domainFromDb;
        }
    }

    protected abstract String getFindDomainQuery();
}
