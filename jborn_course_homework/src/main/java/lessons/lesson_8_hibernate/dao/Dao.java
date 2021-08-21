package lessons.lesson_8_hibernate.dao;

import lessons.lesson_8_hibernate.exceptions.already_exists_exception.*;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;

import java.util.List;

public interface Dao<DOMAIN, ID> {
    DOMAIN findById(ID id) throws OperationFailedException;
    List<DOMAIN> findAll() throws OperationFailedException;
    DOMAIN insert(DOMAIN domain) throws DataAlreadyExistsException, OperationFailedException;
    DOMAIN update(DOMAIN domain) throws DataNotFoundException, OperationFailedException;
    void delete(DOMAIN domain) throws DataNotFoundException, OperationFailedException;
    int deleteAll() throws OperationFailedException;
}
