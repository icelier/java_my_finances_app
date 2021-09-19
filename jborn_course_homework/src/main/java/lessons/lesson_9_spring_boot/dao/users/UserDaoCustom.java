package lessons.lesson_9_spring_boot.dao.users;

import lessons.lesson_9_spring_boot.entities.users.UserEntity;

public interface UserDaoCustom {
    void detach(UserEntity user);
}
