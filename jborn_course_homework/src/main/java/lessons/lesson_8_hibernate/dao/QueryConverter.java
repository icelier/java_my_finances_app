package lessons.lesson_8_hibernate.dao;

import lessons.lesson_8_hibernate.entities.DatabaseEntity;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;

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
