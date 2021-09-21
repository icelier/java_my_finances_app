package lessons.lesson_10_spring_security.controllers;

import lessons.lesson_10_spring_security.exceptions.not_found_exception.DataNotFoundException;

public interface DomainConverter<DOMAIN_REQ, DOMAIN, DOMAIN_RESP> {
    DOMAIN convertDomainFromRequest(DOMAIN_REQ request) throws DataNotFoundException;
    DOMAIN_RESP convertDomainToResponse(DOMAIN domain);
}
