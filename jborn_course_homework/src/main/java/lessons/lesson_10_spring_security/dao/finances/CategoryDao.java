package lessons.lesson_10_spring_security.dao.finances;

import lessons.lesson_10_spring_security.entities.finances.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CategoryDao extends JpaRepository<Category, Long>, CategoryDaoCustom {
    Optional<Category> findByTitle(String title);

    @Modifying
    @Query("update Category cat set cat.title = ?1 where cat.id = ?2")
    int updateCategoryById(String title, Long id);

    @Modifying()
    @Query("delete Category cat where cat.id = ?1")
    int deleteCategoryById(Long id);

    @Modifying()
    @Query("delete Category cat")
    int deleteAllCategories();
}
