package lessons.lesson_4_add_layers_and_factories.dao;

import java.sql.SQLException;
import java.util.List;

public interface Dao<DOMAIN, ID> {
    DOMAIN findById(ID id) throws SQLException, Exception;
    List<DOMAIN> findAll() throws SQLException, Exception;
    DOMAIN insert(DOMAIN domain) throws SQLException;
    DOMAIN update(DOMAIN domain) throws SQLException;
    boolean delete(DOMAIN domain) throws SQLException;
}
