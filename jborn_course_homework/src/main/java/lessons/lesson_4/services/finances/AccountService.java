package lessons.lesson_4.services.finances;

import lessons.lesson_4.entities.finances.Account;

import java.sql.SQLException;
import java.util.List;

public interface AccountService {
    Account findById(Long id) throws SQLException;
    List<Account> findAll() throws SQLException;
    Account insert(Account account) throws SQLException;
    Account update(Account account) throws SQLException;
    boolean delete(Account account) throws SQLException;
}
