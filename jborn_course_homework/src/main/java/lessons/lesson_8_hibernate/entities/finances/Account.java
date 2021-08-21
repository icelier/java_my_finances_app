package lessons.lesson_8_hibernate.entities.finances;

import lessons.lesson_8_hibernate.entities.users.UserEntity;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

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
    @JoinColumn(name = "type_id")
    private AccountType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<Transaction> transactions;

    @Version
    @Column(name="version")
    private Long version;

    public Long getVersion() { return version; }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total.setScale(2, RoundingMode.HALF_UP);
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Account() {}

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
    public String toString() {
        return String.format("Account: %s type %s, total: %.2f", name, type.getTitle(), total.doubleValue());
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
