package lessons.lesson_8_hibernate.dao.users;

import lessons.lesson_8_hibernate.dao.AbstractDao;
import lessons.lesson_8_hibernate.entities.users.Role;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.DataAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.RoleAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.RoleNotFoundException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;

@Repository
public class RoleDao extends AbstractDao<Role,  Long> {
    private static final Logger logger = LoggerFactory.getLogger(RoleDao.class);

    private final EntityManager entityManager;

    public RoleDao(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Role findById(Long id) throws OperationFailedException {
        Role role;
        try {
            role = entityManager.find(Role.class, id);
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return role;
    }

    public Role findByName(String name) throws RoleNotFoundException, OperationFailedException {
        Role role;
        try {
            Query query = entityManager.createQuery(getFindByNameQuery(), Role.class);
            query.setParameter("name", name);
            role = (Role) query.getSingleResult();
        } catch (NoResultException e) {
            throw new RoleNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return role;
    }

    @Override
    public List<Role> findAll() throws OperationFailedException {
        List<Role> roles;
        try {
            TypedQuery<Role> query = entityManager.createQuery(getFindAllQuery(), Role.class);
            roles = query.getResultList();
        } catch (Exception e) {
            throw new OperationFailedException(e.getMessage());
        }

        return roles;
    }

    @Override
    public Role insert(Role role) throws OperationFailedException, RoleAlreadyExistsException {
        try {
            super.insert(role);
        } catch (DataAlreadyExistsException e) {
            throw new RoleAlreadyExistsException(e.getMessage());
        }

        return role;
    }

    @Override
    public Role update(Role role) throws RoleNotFoundException, OperationFailedException {
        entityManager.getTransaction().begin();
        Role roleFromDb = findById(role.getId());
        if (roleFromDb == null) {
            throw new RoleNotFoundException("Role " + role.getName() +  " not found in the database");
        }
        roleFromDb = executeUpdateQuery(roleFromDb, role);
        entityManager.getTransaction().commit();

        return roleFromDb;
    }

    @Override
    public void delete(Role role) throws DataNotFoundException, OperationFailedException {
        Role roleFromDb = findById(role.getId());
        if (roleFromDb == null) {
            throw new RoleNotFoundException("Role " + role.getName() +  " not found in the database");
        }
        super.delete(role);
    }

    @Override
    public int deleteAll() throws OperationFailedException {
        List<Role> roles = findAll();
        int deletedRows = 0;
        if (!roles.isEmpty()) {
            deletedRows = super.deleteAll();
        }

        return deletedRows;
    }

    @Override
    protected String getFindByIdQuery() {
        return "SELECT r FROM Role r WHERE r.id=:id";
    }

    private String getFindByNameQuery() {
        return "SELECT r FROM Role r WHERE r.name=:name";
    }

    @Override
    protected String getFindAllQuery() {
        return "FROM Role r ORDER BY r.id ASC";
    }

    @Override
    protected String getDeleteAllQuery() {
        return "DELETE FROM Role r";
    }

    @Override
    public void updateDomain(Role persistentRole, Role role) {
        persistentRole.setName(role.getName());
    }
}
