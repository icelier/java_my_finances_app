package lessons.lesson_4_add_layers_and_factories.services;

import java.sql.SQLException;
import java.util.List;

public interface Service<DOMAIN, ID> {
    DOMAIN findById(ID id) throws Exception;
    List<DOMAIN> findAll() throws Exception;
    DOMAIN insert(DOMAIN transaction) throws SQLException;
    DOMAIN update(DOMAIN transaction) throws SQLException;
    boolean delete(DOMAIN transaction) throws SQLException;
}
