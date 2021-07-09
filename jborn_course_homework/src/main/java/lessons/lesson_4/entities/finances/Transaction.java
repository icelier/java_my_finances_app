package lessons.lesson_4.entities.finances;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
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
}
