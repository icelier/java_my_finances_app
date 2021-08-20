package lessons.lesson_8_hibernate.entities.finances;

import lessons.lesson_8_hibernate.entities.DatabaseEntity;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.cglib.core.GeneratorStrategy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

@Entity
@Check(constraints = "sum > 0")
@Table(name = "transactions")
public class Transaction implements DatabaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sum", precision = 15, scale = 2, nullable = false)
    private BigDecimal sum;

    @Column(name = "type", nullable = false, length = 25)
    @Enumerated(EnumType.STRING)
    private Operation operation;

    @Column(name = "ts", nullable = false, updatable = false)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Instant timestamp;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum.setScale(2, RoundingMode.HALF_UP);
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }


    public Transaction() { }

    public Transaction(Long id, BigDecimal sum, Operation operation, Account account, Category category) {
        this(sum, operation, account, category);
        this.id = id;
    }

    public Transaction(BigDecimal sum, Operation operation, Account account, Category category) {
        this();
        this.sum = sum.setScale(2, RoundingMode.HALF_UP);
        this.operation = operation;
        this.account = account;
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return getSum().equals(that.getSum()) && getOperation() == that.getOperation() &&
                getTimestamp().equals(that.getTimestamp()) &&
                getAccount().equals(that.getAccount()) &&
                getCategory().equals(that.getCategory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSum(), getOperation(), getTimestamp(), getAccount(), getCategory());
    }

    @Override
    public String toString() {
        return "Transaction{" +
                operation +
                ": sum=" + sum +
                ", timestamp=" + timestamp +
                ", category =" + category +
                '}';
    }

    @Override
    public Long getEntityId() {
        return getId();
    }

    @Override
    public void setEntityId(Long id) {
        setId(id);
    }
}
