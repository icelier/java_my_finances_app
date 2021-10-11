package com.chalova.irina.myfinances.finance_service.dao.accounts;

import com.chalova.irina.myfinances.finance_service.entities.accounts.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountDao extends JpaRepository<Account, Long>, AccountDaoCustom {

    List<Account> findAllByUserName(String userName);
    Optional<Account> findByName(String name);

    Optional<Account> findByUserNameAndName(String userName, String name);

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
