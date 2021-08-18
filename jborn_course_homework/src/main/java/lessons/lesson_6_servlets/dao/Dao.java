package lessons.lesson_6_servlets.dao;

import lessons.lesson_6_servlets.entities.DatabaseEntity;
import lessons.lesson_6_servlets.exceptions.already_exists_exception.*;
import lessons.lesson_6_servlets.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_6_servlets.exceptions.not_found_exception.RoleNotFoundException;
import lessons.lesson_6_servlets.exceptions.not_match_exceptions.QueryNotMatchException;
import lessons.lesson_6_servlets.exceptions.operation_failed.OperationFailedException;

import java.sql.SQLException;
import java.util.List;

public interface Dao<DOMAIN extends DatabaseEntity, ID> {
    DOMAIN findById(ID id) throws SQLException, Exception;
    List<DOMAIN> findAll() throws SQLException, Exception;
    DOMAIN insert(DOMAIN domain) throws SQLException, QueryNotMatchException, OperationFailedException, RoleNotFoundException, AccountAlreadyExistsException, AccountTypeAlreadyExistsException, CategoryAlreadyExistsException, TransactionAlreadyExistsException, RoleAlreadyExistsException, UserAlreadyExistsException;
    DOMAIN update(DOMAIN domain) throws SQLException, QueryNotMatchException, DataNotFoundException, OperationFailedException;
    void delete(DOMAIN domain) throws SQLException, QueryNotMatchException, OperationFailedException;
}
