package lessons.lesson_4.services.finances;

import lessons.lesson_4.dao.finances.TransactionDao;
import lessons.lesson_4.entities.finances.Transaction;

import java.sql.SQLException;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {
    private TransactionDao transactionDao;

    public TransactionServiceImpl(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    @Override
    public Transaction findById(Long id) throws SQLException {
        return transactionDao.findById(id);
    }

    @Override
    public List<Transaction> findAll() throws SQLException {
        return transactionDao.findAll();
    }

    @Override
    public Transaction insert(Transaction transaction) throws SQLException {
        return transactionDao.insert(transaction);
    }

    @Override
    public Transaction update(Transaction transaction) throws SQLException {
        return transactionDao.update(transaction);
    }

    @Override
    public boolean delete(Transaction transaction) throws SQLException {
        return transactionDao.delete(transaction);
    }

    public List<Transaction> findAllByUserId(Long userId) throws SQLException {
        return transactionDao.findAllByUserId(userId);
    }

    public List<Transaction> findAllByUserIdToday(Long userId) throws SQLException {
        return transactionDao.findAllByUserIdToday(userId);
    }
}
