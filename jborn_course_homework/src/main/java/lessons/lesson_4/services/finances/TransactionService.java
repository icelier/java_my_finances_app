package lessons.lesson_4.services.finances;

import lessons.lesson_4.entities.finances.Transaction;

import java.sql.SQLException;
import java.util.List;

public interface TransactionService {
    Transaction findById(Long id) throws SQLException;
    List<Transaction> findAll() throws SQLException;
    Transaction insert(Transaction transaction) throws SQLException;
    Transaction update(Transaction transaction) throws SQLException;
    boolean delete(Transaction transaction) throws SQLException;
}
