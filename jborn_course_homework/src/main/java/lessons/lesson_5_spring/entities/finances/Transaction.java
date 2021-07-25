package lessons.lesson_5_spring.entities.finances;

import lessons.lesson_5_spring.entities.DatabaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction implements DatabaseEntity {
    public static final String TIMESTAMP_FORMAT = "yyyy:mm:dd HH:mm:ss";

    private Long id;

    private BigDecimal sum;

    private Operation operation;

    private LocalDateTime timestamp;

    private Account account;

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
        this.sum = sum;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(Long id, BigDecimal sum, Operation operation, Account account, Category category) {
        this.id = id;
        this.sum = sum;
        this.operation = operation;
        this.account = account;
        this.category = category;
        this.timestamp = LocalDateTime.now();
    }

        public Transaction(BigDecimal sum, Operation operation, Account account, Category category) {
        this.sum = sum;
        this.operation = operation;
        this.account = account;
        this.category = category;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return getSum().equals(that.getSum()) && getOperation() == that.getOperation() && getTimestamp().equals(that.getTimestamp()) && getAccount().equals(that.getAccount()) && getCategory().equals(that.getCategory());
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
                ", category=" + category.getTitle() +
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
