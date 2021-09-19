package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.Account;
import lessons.lesson_9_spring_boot.entities.finances.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;


@Repository
public interface AccountTypeDao extends JpaRepository<AccountType, Long>, AccountTypeDaoCustom {
    Optional<AccountType> findByTitle(String title);

    @Modifying
    @Query("update AccountType at set at.title = ?1 where at.id = ?2")
    int updateAccountTypeById(String title, Long id);

    @Modifying()
    @Query("delete AccountType at where at.id = ?1")
    int deleteAccountTypeById(Long id);

    @Modifying()
    @Query("delete AccountType at")
    int deleteAllAccountTypes();
}
