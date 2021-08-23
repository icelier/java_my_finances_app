package lessons.lesson_9_spring_boot.dao.users;

import lessons.lesson_9_spring_boot.entities.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao extends JpaRepository<Role,  Long> {
    Role findByName(String name);
}
