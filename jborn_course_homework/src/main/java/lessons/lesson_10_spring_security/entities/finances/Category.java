package lessons.lesson_10_spring_security.entities.finances;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 50, nullable = false, unique = true)
    private String title;

    public Category(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Category(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return getTitle().equals(category.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle());
    }

}
