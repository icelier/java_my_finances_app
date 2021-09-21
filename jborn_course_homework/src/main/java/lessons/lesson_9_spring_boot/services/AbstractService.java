package lessons.lesson_9_spring_boot.services;

import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.DataAlreadyExistsException;

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
