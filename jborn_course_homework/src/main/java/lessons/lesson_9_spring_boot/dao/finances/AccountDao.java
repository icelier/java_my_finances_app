package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountDao extends JpaRepository<Account, Long> {
    List<Account> findAllByUserId(Long userId);
    Account findByName(String name);
    Account findByUserIdAndName(Long userId, String name);
}
