package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDao extends JpaRepository<Transaction, Long> {
    @Query("SELECT tr FROM Transaction tr WHERE tr.account.user.id = ?1")
    List<Transaction> findAllByUserId(Long userId);

    @Query(nativeQuery = true, value = "SELECT tr FROM Transaction tr WHERE tr.account.user.id = :userId " +
            "AND tr.ts BETWEEN :beginTime AND :endTime")
    List<Transaction> findAllByUserIdToday(Long userId, String beginTime, String endTime);
}
