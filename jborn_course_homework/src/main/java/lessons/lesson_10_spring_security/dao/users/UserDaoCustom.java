package lessons.lesson_10_spring_security.dao.users;

import lessons.lesson_10_spring_security.entities.users.UserEntity;

public interface UserDaoCustom {
    void detach(UserEntity user);
}
