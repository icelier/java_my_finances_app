package lessons.lesson_8_hibernate.dao;

import lessons.lesson_8_hibernate.exceptions.already_exists_exception.*;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;

import javax.persistence.*;
import java.util.List;

public abstract class AbstractDao<DOMAIN, ID> {

    protected final EntityManager entityManager;

    protected AbstractDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected abstract String getFindByIdQuery();
    protected abstract String getFindAllQuery();
    protected abstract String getDeleteAllQuery();

    protected abstract DOMAIN findById(ID id) throws OperationFailedException;
    protected abstract List<DOMAIN> findAll() throws OperationFailedException;
    protected abstract DOMAIN update(DOMAIN domain) throws DataNotFoundException, OperationFailedException;

    protected DOMAIN insert(DOMAIN domain) throws DataAlreadyExistsException, OperationFailedException {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            executeInsertQuery(domain);
            entityManager.getTransaction().commit();
        } catch (DataAlreadyExistsException e) {
            throw new DataAlreadyExistsException(e.getMessage());
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        } finally {
            rollbackTransaction(transaction);
        }

        return domain;
    }
    protected void delete(DOMAIN persistentDomain) throws DataNotFoundException, OperationFailedException {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            executeDeleteQuery(persistentDomain);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        } finally {
            rollbackTransaction(transaction);
        }
    }
    protected int deleteAll() throws OperationFailedException {
        EntityTransaction transaction = null;
        int deletedRows = 0;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            deletedRows = executeDeleteAllQuery();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        } finally {
            rollbackTransaction(transaction);
        }

        return deletedRows;
    }

    protected abstract void updateDomain(DOMAIN persistentDomain, DOMAIN domain);

    protected DOMAIN executeUpdateQuery(DOMAIN persistentDomain, DOMAIN domain) {
        updateDomain(persistentDomain, domain);

        return persistentDomain;
    }

    protected void executeDeleteQuery(DOMAIN persistentDomain) throws OperationFailedException {
        try {
            entityManager.remove(persistentDomain);
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }
    }

    protected int executeDeleteAllQuery() throws OperationFailedException {
        int deletedRows = 0;
        try {
            Query deleteAllQuery = entityManager.createQuery(getDeleteAllQuery());
            deletedRows = deleteAllQuery.executeUpdate();
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }
        return deletedRows;
    }

    protected void executeInsertQuery(DOMAIN domain) throws DataAlreadyExistsException, OperationFailedException {
        try {
            entityManager.persist(domain);
        } catch (EntityExistsException e) {
            throw new DataAlreadyExistsException(e.getMessage());
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }
    }

    protected void rollbackTransaction(EntityTransaction transaction) throws OperationFailedException {
        if (transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (Exception ex) {
                throw new OperationFailedException(ex.getMessage());
            }
        }
    }

}
