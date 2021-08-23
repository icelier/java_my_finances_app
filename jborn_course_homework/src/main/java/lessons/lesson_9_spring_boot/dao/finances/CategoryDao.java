package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryDao extends JpaRepository<Category, Long> {
    Category findByTitle(String title);
}
