package lessons.lesson_3_add_dao.entities.finances;

import lessons.lesson_3_add_dao.entities.users.UserEntity;

import java.math.BigDecimal;

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
}
