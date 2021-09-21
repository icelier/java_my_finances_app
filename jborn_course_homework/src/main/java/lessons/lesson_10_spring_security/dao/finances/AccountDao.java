package lessons.lesson_10_spring_security.dao.finances;

import lessons.lesson_10_spring_security.entities.finances.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountDao extends JpaRepository<Account, Long>, AccountDaoCustom {
    List<Account> findAllByUserId(Long userId);
    Optional<Account> findByName(String name);
    Optional<Account> findByUserIdAndName(Long userId, String name);

    @Modifying()
    @Query("update Account a set a.name = ?1 where a.id = ?2")
    int updateAccountNameById(String name, Long id);

    @Modifying()
    @Query("update Account a set a.total = ?1 where a.id = ?2")
    int updateAccountTotalById(BigDecimal total, Long id);

    @Modifying()
    @Query("delete Account a where a.id = ?1")
    int deleteAccountById(Long id);

    @Modifying()
    @Query("delete Account a")
    int deleteAllAccounts();
}
