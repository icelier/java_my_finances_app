package lessons.lesson_8_hibernate.services;

import lessons.lesson_8_hibernate.exceptions.already_exists_exception.AccountAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.CategoryAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.TransactionAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.*;
import lessons.lesson_8_hibernate.exceptions.not_match_exceptions.QueryNotMatchException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;

import java.sql.SQLException;
import java.util.List;

public interface AbstractService<DOMAIN, ID> {
    DOMAIN findById(ID id) throws Exception;
    List<DOMAIN> findAll() throws Exception;
    DOMAIN insert(DOMAIN transaction) throws SQLException, UserAlreadyExistsException, QueryNotMatchException, OperationFailedException, RoleNotFoundException, AccountAlreadyExistsException, TransactionAlreadyExistsException, CategoryAlreadyExistsException;
    DOMAIN update(DOMAIN transaction) throws SQLException, QueryNotMatchException, AccountNotFoundException, UserNotFoundException, OperationFailedException, TransactionNotFoundException, CategoryNotFoundException;
    void delete(DOMAIN transaction) throws SQLException, QueryNotMatchException, OperationFailedException;
}
