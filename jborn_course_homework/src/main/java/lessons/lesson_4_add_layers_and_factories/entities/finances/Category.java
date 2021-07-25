package lessons.lesson_4_add_layers_and_factories.entities.finances;

public class Category {

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
}
