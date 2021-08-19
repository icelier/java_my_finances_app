package lessons.lesson_8_hibernate.entities.finances;

import lessons.lesson_8_hibernate.entities.DatabaseEntity;
import lessons.lesson_8_hibernate.entities.finances.Account;
import lessons.lesson_8_hibernate.entities.finances.Category;
import lessons.lesson_8_hibernate.entities.finances.Operation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

public class Transaction implements DatabaseEntity {
//    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnn");

    private Long id;

    private BigDecimal sum;

    private Operation operation;

    private Instant timestamp;

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


    public Transaction() {
        this.timestamp = Instant.now();
    }

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
