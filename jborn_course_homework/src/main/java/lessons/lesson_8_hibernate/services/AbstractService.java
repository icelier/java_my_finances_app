package lessons.lesson_8_hibernate.services;

import lessons.lesson_8_hibernate.exceptions.already_exists_exception.*;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.*;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;

import java.util.List;

public abstract class AbstractService<DOMAIN, ID> {
    public abstract DOMAIN findById(ID id) throws OperationFailedException;
    public abstract List<DOMAIN> findAll() throws OperationFailedException;
    public abstract DOMAIN insert(DOMAIN transaction) throws OperationFailedException, DataNotFoundException, DataAlreadyExistsException;
    public abstract DOMAIN update(DOMAIN transaction) throws DataNotFoundException, DataAlreadyExistsException, OperationFailedException;
    public abstract void delete(DOMAIN transaction) throws OperationFailedException, DataNotFoundException;
    public abstract int deleteAll() throws OperationFailedException;
}
