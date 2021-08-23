package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountTypeDao extends JpaRepository<AccountType, Long> {
    AccountType findByTitle(String title);
}
