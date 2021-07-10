package lessons.lesson_4.services.users;

import lessons.lesson_4.entities.users.UserEntity;
import lessons.lesson_4.entities.users.UserProjection;

import java.sql.SQLException;
import java.util.List;

public interface UserService {
    UserEntity findById(Long id) throws SQLException;
    List<UserEntity> findAll() throws SQLException;
    UserEntity insert(UserEntity domain) throws SQLException;
    UserEntity update(UserEntity domain) throws SQLException;
    boolean delete(UserEntity domain) throws SQLException;
}
