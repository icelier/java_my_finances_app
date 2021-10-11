package com.chalova.irina.myfinances.finance_service.entities.accounts;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "accounts", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ColumnDefault("0")
    @Column(name = "total", precision = 15, scale = 2, nullable = false)
    private BigDecimal total;

    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", updatable = false)
    private AccountType type;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userName;

    @Version
    @Column(name="version")
    private Long version;

    public void setTotal(BigDecimal total) {
        this.total = total.setScale(2, RoundingMode.HALF_UP);
    }

    public Account(String name, BigDecimal total, AccountType type, String userName) {
        this.name = name;
        this.total = total.setScale(2, RoundingMode.HALF_UP);
        this.type = type;
        this.userName = userName;
    }

    public Account(Long id, String name, BigDecimal total, AccountType type, String userName) {
        this(name, total, type, userName);
        this.id = id;
    }

    public Account(String name, AccountType type, String userName) {
        this(name, BigDecimal.ZERO, type, userName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return getName().equals(account.getName()) && getType().equals(account.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTotal(), getType(), getUserName());
    }

}
