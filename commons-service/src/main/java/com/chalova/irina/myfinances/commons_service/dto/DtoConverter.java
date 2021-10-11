package com.chalova.irina.myfinances.commons_service.dto;

import com.chalova.irina.myfinances.commons_service.exceptions.not_found_exception.DataNotFoundException;

public interface DtoConverter<DTO, DOMAIN> {
    DOMAIN convertDomainFromDto(DTO dto) throws DataNotFoundException;
    DTO convertDomainToDto(DOMAIN domain);
}
