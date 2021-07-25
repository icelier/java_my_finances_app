package lessons.lesson_4_add_layers_and_factories.entities.finances;

import lessons.lesson_4_add_layers_and_factories.entities.users.UserEntity;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    private Long id;

    private String name;

    private BigDecimal sum;

    private AccountType type;

    private UserEntity user;

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
        this.sum = sum;
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

    public Account(Long id, String name, BigDecimal sum, AccountType type, UserEntity user) {
        this.id = id;
        this.name = name;
        this.sum = sum;
        this.type = type;
        this.user = user;
    }

    public Account(String name, BigDecimal sum, AccountType type, UserEntity user) {
        this.name = name;
        this.sum = sum;
        this.type = type;
        this.user = user;
    }
    public Account(String name, AccountType type, UserEntity user) {
        this.name = name;
        this.sum = BigDecimal.ZERO;
        this.type = type;
        this.user = user;
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
        return getId().equals(account.getId()) && getName().equals(account.getName()) && getSum().equals(account.getSum()) && getType().equals(account.getType()) && getUser().equals(account.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getSum(), getType(), getUser());
    }
}
