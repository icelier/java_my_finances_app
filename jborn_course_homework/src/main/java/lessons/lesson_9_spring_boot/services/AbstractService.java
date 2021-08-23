package lessons.lesson_9_spring_boot.services;

import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.*;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.*;
import lessons.lesson_9_spring_boot.exceptions.operation_failed.OperationFailedException;

import java.util.List;

public interface AbstractService<DOMAIN, ID> {
    DOMAIN findById(ID id);
    List<DOMAIN> findAll();
    DOMAIN insert(DOMAIN transaction) throws DataAlreadyExistsException;
    DOMAIN update(DOMAIN transaction);
    void delete(DOMAIN transaction);
    void deleteAll();
}
