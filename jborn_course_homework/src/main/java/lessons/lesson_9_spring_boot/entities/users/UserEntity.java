package lessons.lesson_9_spring_boot.entities.users;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lessons.lesson_9_spring_boot.entities.finances.Account;
import lessons.lesson_9_spring_boot.entities.users.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String userName;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "fullname", length = 100, nullable = false)
    private String fullName;

    @Column(name = "age")
    private int age;

    @Column(name = "email", length = 50, nullable = false, unique = true, updatable = false)
    private String email;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Account> accounts;

    public UserEntity(String userName, String password, String email) {
        this.userName = userName;
        this.fullName = userName;
        this.password = password;
        this.email = email;
    }

    public UserEntity(String userName, String fullName, String password, String email, int age) {
        this(userName, password, email);
        this.fullName = fullName;
        this.age = age;
    }

    public UserEntity(Long id, String userName, String fullName, String password, String email, int age) {
        this(userName, fullName, password, email, age);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return getEmail().equals(that.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }

}