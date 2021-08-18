package lessons.lesson_7_controllers.dao;

import lessons.lesson_7_controllers.entities.DatabaseEntity;
import lessons.lesson_7_controllers.exceptions.operation_failed.OperationFailedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface QueryConverter<DOMAIN extends DatabaseEntity, ID> {
    default PreparedStatement setupQuery(PreparedStatement ps) {return ps;}

    void setupFindByIdQuery(PreparedStatement ps, ID id) throws SQLException;
    void setupInsertQuery(PreparedStatement ps, DOMAIN domain) throws SQLException;
    void setupDeleteQuery(PreparedStatement ps, DOMAIN domain) throws SQLException;
    default void setupUpdateQuery(PreparedStatement ps, DOMAIN domain) throws SQLException {}

    DOMAIN getDomainFromQueryResult(ResultSet rs) throws SQLException;
    void updateDomain(ResultSet rs, DOMAIN domain) throws SQLException, OperationFailedException;

    void setupFindDomainQuery(PreparedStatement ps, DOMAIN domain) throws SQLException;
}
