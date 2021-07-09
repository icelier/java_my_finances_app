package lessons.lesson_4.services;

import java.util.List;

public interface AbstractService<DOMAIN> {
    // no SQLException in method signatures, all exceptions processed at repository(dao) layer
    <ID> DOMAIN findById(ID id);
    List<DOMAIN> findAll();
    DOMAIN insert(DOMAIN domain);
    DOMAIN update(DOMAIN domain);
    boolean delete(DOMAIN domain);
}
