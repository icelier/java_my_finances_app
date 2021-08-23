package lessons.lesson_9_spring_boot.controllers;

import lessons.lesson_9_spring_boot.exceptions.not_found_exception.DataNotFoundException;

public interface DomainConverter<DOMAIN_REQ, DOMAIN, DOMAIN_RESP> {
    DOMAIN convertDomainFromRequest(DOMAIN_REQ request) throws DataNotFoundException;
    DOMAIN_RESP convertDomainToResponse(DOMAIN domain);
}
