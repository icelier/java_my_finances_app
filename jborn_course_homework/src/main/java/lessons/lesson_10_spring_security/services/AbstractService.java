package lessons.lesson_10_spring_security.services;

import lessons.lesson_10_spring_security.exceptions.already_exists_exception.DataAlreadyExistsException;
import lessons.lesson_10_spring_security.exceptions.not_found_exception.DataNotFoundException;

import java.util.List;

public interface AbstractService<DOMAIN, ID> {
    DOMAIN findById(ID id);
    List<DOMAIN> findAll();
    DOMAIN insert(DOMAIN transaction) throws DataAlreadyExistsException;
    DOMAIN update(ID id, DOMAIN transaction);
    void delete(DOMAIN transaction);
    void deleteAll();

    DOMAIN updateDomainWithNewData(DOMAIN domainToUpdate, DOMAIN updateData);
}
