package lessons.lesson_6_servlets.dao.finances;

import lessons.lesson_6_servlets.dao.AbstractDao;
import lessons.lesson_6_servlets.entities.finances.AccountType;
import lessons.lesson_6_servlets.exceptions.already_exists_exception.AccountTypeAlreadyExistsException;
import lessons.lesson_6_servlets.exceptions.not_found_exception.AccountTypeNotFoundException;
import lessons.lesson_6_servlets.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_6_servlets.exceptions.operation_failed.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AccountTypeDao extends AbstractDao<AccountType, Long> {
    private static final Logger logger = LoggerFactory.getLogger(AccountTypeDao.class);
    private final DataSource dataSource;

    public AccountTypeDao(DataSource dataSource) {
        super(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public AccountType findById(Long id) throws SQLException {
        return executeFindByIdQuery(id);
    }

    /**
     * Returns list of account types
     * @return list of account types or empty list if no any
     */
    @Override
    public List<AccountType> findAll() throws SQLException {
        return executeFindAllQuery();
    }

    /**
     * Returns inserted account
     * @return inserted account if success
     */
    @Override
    public AccountType insert(AccountType accountType) throws SQLException, OperationFailedException, AccountTypeAlreadyExistsException {
        boolean alreadyExists = checkIfAlreadyExists(accountType);
        if (alreadyExists) {
            throw new AccountTypeAlreadyExistsException("Account type already exists");
        }
        return executeInsertQuery(accountType);
    }

    /**
     * Returns updated account type
     * @return updated account type if success
     */
    @Override
    public AccountType update(AccountType accountType) throws SQLException, OperationFailedException, AccountTypeNotFoundException {
        try {
            executeUpdateQuery(accountType.getId(), accountType);
        } catch (DataNotFoundException e) {
            throw  new AccountTypeNotFoundException("Account type not found in the database");
        }

        return accountType;
    }

    @Override
    public void delete(AccountType accountType) throws SQLException, OperationFailedException {
        executeDeleteQuery(accountType);
    }

    @Override
    protected String getInsertQuery() {
        return "INSERT INTO account_types (title) VALUES (?)";
    }

    @Override
    protected String getFindByIdQuery() {
        return "SELECT * FROM account_types WHERE id=?";
    }



    @Override
    protected String getFindAllQuery() {
        return "SELECT * FROM account_types ORDER BY id ASC";
    }

    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM account_types WHERE id=?";
    }

    @Override
    protected String getFindDomainQuery() {
        return "SELECT * FROM account_types WHERE title=?";
    }

    @Override
    public void setupFindByIdQuery(PreparedStatement ps, Long id) throws SQLException {
        ps.setLong(1, id);
    }

    @Override
    public void setupInsertQuery(PreparedStatement ps, AccountType accountType) throws SQLException {
        ps.setString(1, accountType.getTitle());
    }

    @Override
    public void setupDeleteQuery(PreparedStatement ps, AccountType accountType) throws SQLException {
        ps.setLong(1, accountType.getId());
    }

    @Override
    public void setupFindDomainQuery(PreparedStatement ps, AccountType accountType) throws SQLException {
        ps.setString(1, accountType.getTitle());
    }

    @Override
    public AccountType getDomainFromQueryResult(ResultSet rs) throws SQLException {
        AccountType type = new AccountType();
        type.setId(rs.getLong("id"));
        type.setTitle(rs.getString("title"));

        return type;
    }

    @Override
    public void updateDomain(ResultSet rs, AccountType accountType) throws SQLException, OperationFailedException {
        rs.updateString("title", accountType.getTitle());
        rs.updateRow();
    }
}
