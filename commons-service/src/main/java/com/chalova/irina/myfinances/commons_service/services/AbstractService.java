package com.chalova.irina.myfinances.commons_service.services;

import com.chalova.irina.myfinances.commons_service.exceptions.already_exists_exception.DataAlreadyExistsException;

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
