package com.chalova.irina.myfinances.finance_service.dao.transactions;

import com.chalova.irina.myfinances.finance_service.entities.transactions.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDao extends JpaRepository<AccountTransaction, Long>, TransactionDaoCustom {

    @Query("select tr from AccountTransaction tr where tr.account.userName = ?1")
    List<AccountTransaction> findAllByUserName(String userName);

    @Query(nativeQuery = true, value = "SELECT tr.* FROM transactions tr INNER JOIN accounts a ON " +
            "tr.account_id=a.id WHERE a.user_id=:userName " +
            "AND tr.ts BETWEEN :beginTime AND :endTime")
    List<AccountTransaction> findAllByUserNameToday(String userName, String beginTime, String endTime);

    @Query("SELECT tr FROM AccountTransaction tr WHERE tr.account.id = ?1")
    List<AccountTransaction> findAllByAccountId(Long accountId);

    @Modifying()
    @Query("delete AccountTransaction tr where tr.id = ?1")
    int deleteTransactionById(Long id);

    @Modifying()
    @Query("delete AccountTransaction tr")
    int deleteAllTransactions();
}
