package lessons.lesson_9_spring_boot.dao.users;

import lessons.lesson_9_spring_boot.entities.finances.Account;
import lessons.lesson_9_spring_boot.entities.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleDao extends JpaRepository<Role,  Long>, RoleDaoCustom {
    Optional<Role> findByName(String name);

    @Modifying
    @Query("update Role r set r.name = ?1 where r.id = ?2")
    int updateRoleById(String name, Long id);

    @Modifying()
    @Query("delete Role r where r.id = ?1")
    int deleteRoleById(Long id);

    @Modifying()
    @Query("delete Role r")
    int deleteAllRoles();
}
