package lessons.lesson_10_spring_security.dao.users;

import lessons.lesson_10_spring_security.entities.users.Role;

public interface RoleDaoCustom {
    void detach(Role role);
}
