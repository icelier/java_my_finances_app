package lessons.lesson_8_hibernate.dao;

import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;

import javax.persistence.Query;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface QueryConverter<DOMAIN, ID> {

    void setupFindByIdQuery(Query query, ID id) throws SQLException;
    void setupInsertQuery(Query query, DOMAIN domain) throws SQLException;
    void setupDeleteQuery(Query query, DOMAIN domain) throws SQLException;
    default void setupUpdateQuery(Query query, DOMAIN domain) throws SQLException {}

    DOMAIN getDomainFromQueryResult(ResultSet rs) throws SQLException;
    void updateDomain(DOMAIN persistentDomain, DOMAIN domain) throws SQLException, OperationFailedException;

    void setupFindDomainQuery(Query query, DOMAIN domain) throws SQLException;
}
