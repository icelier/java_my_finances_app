package lessons.lesson_9_spring_boot.dao.users;

import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<UserEntity, Long> {
    UserEntity findByUserName(String userName);
    UserEntity findByEmail(String email);
}
