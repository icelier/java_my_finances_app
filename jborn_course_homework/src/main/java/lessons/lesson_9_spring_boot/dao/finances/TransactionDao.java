package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDao extends JpaRepository<Transaction, Long>, TransactionDaoCustom {
    @Query("select tr from Transaction tr where tr.account.user.id = ?1")
    List<Transaction> findAllByUserId(Long userId);

    @Query(nativeQuery = true, value = "SELECT tr.* FROM transactions tr INNER JOIN accounts a ON " +
                            "tr.account_id=a.id WHERE a.user_id=:userId " +
                            "AND tr.ts BETWEEN :beginTime AND :endTime")
    List<Transaction> findAllByUserIdToday(Long userId, String beginTime, String endTime);

    @Modifying()
    @Query("delete Transaction tr where tr.id = ?1")
    int deleteTransactionById(Long id);

    @Modifying()
    @Query("delete Transaction tr")
    int deleteAllTransactions();
}
