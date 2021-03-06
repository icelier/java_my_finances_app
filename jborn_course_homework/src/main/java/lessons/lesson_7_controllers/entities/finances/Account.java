package lessons.lesson_7_controllers.entities.finances;

import lessons.lesson_7_controllers.entities.DatabaseEntity;
import lessons.lesson_7_controllers.entities.users.UserEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Account implements DatabaseEntity {
    private Long id;

    private String name;

    private BigDecimal sum;

    private AccountType type;

    private Long userId;

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

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum.setScale(2, RoundingMode.HALF_UP);
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Account() {}

    public Account(String name, BigDecimal sum, AccountType type, Long userId) {
        this.name = name;
        this.sum = sum.setScale(2, RoundingMode.HALF_UP);
        this.type = type;
        this.userId = userId;
    }

    public Account(Long id, String name, BigDecimal sum, AccountType type, Long userId) {
        this(name, sum, type, userId);
        this.id = id;
    }

    public Account(String name, AccountType type, Long userId) {
        this(name, BigDecimal.ZERO, type, userId);
    }

    @Override
    public String toString() {
        return String.format("Account: %s type %s, total: %.2f", name, type.getTitle(), sum.doubleValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return getName().equals(account.getName()) && getType().equals(account.getType()) &&
                getUserId().equals(account.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getSum(), getType(), getUserId());
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
