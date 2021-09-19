package lessons.lesson_9_spring_boot.dao.finances;

import lessons.lesson_9_spring_boot.entities.finances.Category;

public interface CategoryDaoCustom {
    void detach(Category category);
}
