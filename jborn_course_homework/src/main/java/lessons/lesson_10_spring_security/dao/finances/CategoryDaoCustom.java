package lessons.lesson_10_spring_security.dao.finances;

import lessons.lesson_10_spring_security.entities.finances.Category;

public interface CategoryDaoCustom {
    void detach(Category category);
}
