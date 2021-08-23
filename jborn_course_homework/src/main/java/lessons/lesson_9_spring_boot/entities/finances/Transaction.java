package lessons.lesson_9_spring_boot.entities.finances;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lessons.lesson_9_spring_boot.entities.finances.Account;
import lessons.lesson_9_spring_boot.entities.finances.Category;
import lessons.lesson_9_spring_boot.entities.finances.Operation;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Check(constraints = "sum <> 0")
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sum", precision = 15, scale = 2, nullable = false, updatable = false)
    private BigDecimal sum;

    @Column(name = "type", nullable = false, length = 25, updatable = false)
    @Enumerated(EnumType.STRING)
    private Operation operation;

    @Column(name = "ts", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant timestamp;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @JsonBackReference
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    public void setSum(BigDecimal sum) {
        this.sum = sum.setScale(2, RoundingMode.HALF_UP);
    }

    public Transaction(BigDecimal sum, Operation operation, Account account, Category category) {
        this.sum = sum.setScale(2, RoundingMode.HALF_UP);
        this.operation = operation;
        this.account = account;
        this.category = category;
    }

    public Transaction(Long id, BigDecimal sum, Operation operation,
                       Account account, Category category) {
        this(sum, operation, account, category);
        this.id = id;
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
}
