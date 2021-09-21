package lessons.lesson_10_spring_security.entities.finances;

import lessons.lesson_10_spring_security.entities.users.UserEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "accounts", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @ColumnDefault("0")
    @Column(name = "total", precision = 15, scale = 2, nullable = false)
    private BigDecimal total;

    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", updatable = false)
    private AccountType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<Transaction> transactions;

    @Version
    @Column(name="version")
    private Long version;

    public void setTotal(BigDecimal total) {
        this.total = total.setScale(2, RoundingMode.HALF_UP);
    }

    public Account(String name, BigDecimal total, AccountType type, UserEntity user) {
        this.name = name;
        this.total = total.setScale(2, RoundingMode.HALF_UP);
        this.type = type;
        this.user = user;
    }

    public Account(Long id, String name, BigDecimal total, AccountType type, UserEntity user) {
        this(name, total, type, user);
        this.id = id;
    }

    public Account(String name, AccountType type, UserEntity user) {
        this(name, BigDecimal.ZERO, type, user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return getName().equals(account.getName()) && getType().equals(account.getType()) &&
                getUser().equals(account.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTotal(), getType(), getUser());
    }

}
