package lessons.lesson_8_hibernate.dao.finances;

import lessons.lesson_8_hibernate.dao.AbstractDao;
import lessons.lesson_8_hibernate.entities.finances.Transaction;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.DataAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.TransactionAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.TransactionNotFoundException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class TransactionDao extends AbstractDao<Transaction, Long> {
    private static final Logger logger = LoggerFactory.getLogger(TransactionDao.class);

    private final EntityManager entityManager;
    private final AccountDao accountDao;
    private final CategoryDao categoryDao;

    public TransactionDao(
            EntityManager entityManager,
            AccountDao accountDao,
            CategoryDao categoryDao
    ) {
        super(entityManager);
        this.entityManager = entityManager;
        this.accountDao = accountDao;
        this.categoryDao = categoryDao;
    }

    @Override
    public Transaction findById(Long id) throws OperationFailedException {
        Transaction transaction;
        try {
            transaction = entityManager.find(Transaction.class, id);
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return transaction;
    }

    public List<Transaction> findAllByUserId(Long userId) throws OperationFailedException {
        List<Transaction> transactions;
        try {
            TypedQuery<Transaction> query = entityManager.createQuery(getFindByUserIdQuery(), Transaction.class);
            query.setParameter("userId", userId);
            transactions = query.getResultList();
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return transactions;
    }

    public List<Transaction> findAllByUserIdToday(Long userId) throws OperationFailedException {
        List<Transaction> transactions;
        try {
            TypedQuery<Transaction> query = entityManager.createQuery(getFindByUserIdTodayQuery(), Transaction.class);
            query.setParameter("userId", userId);
            Instant now = Instant.now();
            Instant yesterday = now.minus(1, TimeUnit.DAYS.toChronoUnit());
            String endTime = Timestamp.from(now).toString();
            String beginTime = Timestamp.from(yesterday).toString();
            query.setParameter("beginTime", beginTime);
            query.setParameter("endTime", endTime);
            transactions = query.getResultList();
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return transactions;
    }

    @Override
    public List<Transaction> findAll() throws OperationFailedException {
        List<Transaction> transactions;
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);
            Root<Transaction> root = criteriaQuery.from(Transaction.class);
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("timestamp")));
            transactions = entityManager.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return transactions;
    }

    @Override
    public Transaction insert(Transaction transaction) throws OperationFailedException, TransactionAlreadyExistsException {
        try {
            super.insert(transaction);
        } catch (DataAlreadyExistsException e) {
            throw new TransactionAlreadyExistsException(e.getMessage());
        }

        return transaction;
    }

    public Transaction insert(Transaction transaction, boolean dbTransactionAlreadyBegan) throws OperationFailedException, TransactionAlreadyExistsException {
        if (dbTransactionAlreadyBegan) {
            try {
                entityManager.persist(transaction);
            } catch (EntityExistsException e) {
                throw new TransactionAlreadyExistsException(e.getMessage());
            } catch (Exception e) {
                throw new OperationFailedException(e.getMessage());
            }
        }

        return transaction;
    }

    @Override
    public Transaction update(Transaction transaction) throws TransactionNotFoundException, OperationFailedException {
        boolean hasSuccess = false;
        EntityTransaction entityTransaction = null;
        Transaction transactionFromDb = null;

        while(!hasSuccess) {
            if (entityTransaction != null) {
                logger.debug("Money transfer transactions is active = " + entityTransaction.isActive());
            }
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            transactionFromDb = findById(transaction.getId());
            if (transactionFromDb == null) {
                throw new TransactionNotFoundException("Transaction " + transaction.getSum() + " not found in the database");
            }
            entityManager.lock(transactionFromDb, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            transactionFromDb = executeUpdateQuery(transactionFromDb, transaction);
            try {
                entityTransaction.commit();
                hasSuccess = true;
            } catch (OptimisticLockException e) {
                e.printStackTrace();
                if (entityTransaction.isActive()) {
                    try {
                        entityTransaction.rollback();
                    } catch (Exception ex) {
                        throw new OperationFailedException(ex.getMessage());
                    }
                }
            }
        }

        return transactionFromDb;
    }

    @Override
    public void delete(Transaction transaction) throws OperationFailedException, DataNotFoundException {
        Transaction transactionFromDb = findById(transaction.getId());
        if (transactionFromDb == null) {
            throw new TransactionNotFoundException("Transaction " + transaction.getSum() + " not found in the database");
        }
        super.delete(transaction);
    }

    @Override
    public int deleteAll() throws OperationFailedException {
        List<Transaction> transactions = findAll();
        int deletedRows = 0;
        if (!transactions.isEmpty()) {
            deletedRows = super.deleteAll();
        }

        return deletedRows;
    }

    @Override
    protected String getFindByIdQuery() {
        return "SELECT tr FROM Transaction tr WHERE tr.id=:id";
    }

    private String getFindByUserIdQuery() {
        return "SELECT tr FROM Transaction tr WHERE tr.account.user.id=:userId";
    }

    @Override
    protected String getDeleteAllQuery() {
        return "DELETE FROM Transaction tr";
    }

    private String getFindByUserIdTodayQuery() {
        return "SELECT tr FROM Transaction tr WHERE tr.account.user.id=:userId " +
                "AND tr.ts BETWEEN :beginTime AND :endTime";
    }

    @Override
    protected String getFindAllQuery() {
        return "SELECT tr FROM Transaction tr ORDER BY tr.ts DESC";
    }

    @Override
    public void updateDomain(Transaction persistentTransaction, Transaction transaction) {

    }
}
