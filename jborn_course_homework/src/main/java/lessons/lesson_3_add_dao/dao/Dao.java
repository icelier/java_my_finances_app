package lessons.lesson_3_add_dao.dao;

import java.sql.SQLException;
import java.util.List;

public interface Dao<DOMAIN, ID> {
    DOMAIN findById(ID id) throws SQLException;
    List<DOMAIN> findAll() throws SQLException;
    DOMAIN insert(DOMAIN domain) throws SQLException;
    DOMAIN update(DOMAIN domain) throws SQLException;
    boolean delete(DOMAIN domain) throws SQLException;
}
