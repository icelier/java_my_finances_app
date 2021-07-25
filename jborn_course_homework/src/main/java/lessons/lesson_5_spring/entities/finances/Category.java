package lessons.lesson_5_spring.entities.finances;

import lessons.lesson_5_spring.entities.DatabaseEntity;

import java.util.Objects;

public class Category implements DatabaseEntity {

    private Long id;

    private String title;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category() {}

    public Category(Long id, String title) {
        this.id = id;
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

    @Override
    public Long getEntityId() {
        return getId();
    }

    @Override
    public void setEntityId(Long id) {
        setId(id);
    }
}
