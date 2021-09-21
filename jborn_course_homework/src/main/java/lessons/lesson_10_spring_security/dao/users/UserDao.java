package lessons.lesson_10_spring_security.dao.users;

import lessons.lesson_10_spring_security.entities.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<UserEntity, Long>, UserDaoCustom {
    Optional<UserEntity> findByUserName(String userName);
    Optional<UserEntity> findByEmail(String email);

    // TODO - add test
    @Query("select u from UserEntity u join fetch u.roles where u.userName = ?1")
    Optional<UserEntity> findUser(String userName);

    @Modifying
    @Query("update UserEntity u set u.userName = ?1 where u.id = ?2")
    int updateUserNameById(String userName, Long id);

    @Modifying
    @Query("update UserEntity u set u.fullName = ?1 where u.id = ?2")
    int updateUserFullNameById(String fullName, Long id);

    @Modifying
    @Query("update UserEntity u set u.password = ?1 where u.id = ?2")
    int updateUserPasswordById(String password, Long id);

    @Modifying
    @Query("update UserEntity u set u.age = ?1 where u.id = ?2")
    int updateUserAgeById(int age, Long id);

//    @Modifying
//    @Query("update UserEntity u set u.userName = ?1, u.fullName = ?2, u.password = ?3, u.age = ?4 where u.id = ?5")
//    int updateUserById(String userName, String fullName, String password, int age, Long id);

    @Modifying()
    @Query("delete UserEntity u where u.id = ?1")
    int deleteUserById(Long id);

    @Modifying()
    @Query("delete UserEntity u")
    int deleteAllUsers();
}
