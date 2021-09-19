package lessons.lesson_9_spring_boot.dao.users;

import lessons.lesson_9_spring_boot.entities.users.Role;

public interface RoleDaoCustom {
    void detach(Role role);
}
