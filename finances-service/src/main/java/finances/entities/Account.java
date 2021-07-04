package finances.entities;

import users.entities.UserEntity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "bank")
    private String bankName;

    @Column(name = "total")
    private BigDecimal sum;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private AccountType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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

    public Account(Long id, String bankName, BigDecimal sum, AccountType type, UserEntity user) {
        this.id = id;
        this.bankName = bankName;
        this.sum = sum;
        this.type = type;
        this.user = user;
    }
}
