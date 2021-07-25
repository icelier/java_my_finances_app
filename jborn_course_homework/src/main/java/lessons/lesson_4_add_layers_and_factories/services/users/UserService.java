package lessons.lesson_4_add_layers_and_factories.services.users;

import lessons.lesson_4_add_layers_and_factories.dao.users.UserDao;
import lessons.lesson_4_add_layers_and_factories.entities.users.UserEntity;
import lessons.lesson_4_add_layers_and_factories.services.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;
import java.util.List;

public class UserService implements Service<UserEntity, Long> {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserEntity findById(Long id) throws SQLException {
        return userDao.findById(id);
    }

    @Override
    public List<UserEntity> findAll() throws SQLException {
        return userDao.findAll();
    }

    @Override
    public UserEntity insert(UserEntity user) throws SQLException {
        UserEntity userFRomDb = findByUserNameAndPassword(user.getUserName(), user.getPassword());
        if (userFRomDb != null) {
            return null;
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userDao.insert(user);
    }

    @Override
    public UserEntity update(UserEntity user) throws SQLException {
        return userDao.update(user);
    }

    @Override
    public boolean delete(UserEntity user) throws SQLException {
        return userDao.delete(user);
    }

    public UserEntity findByUserNameAndPassword(String name, String password) throws SQLException {

        UserEntity user = userDao.findByUserName(name);
        if (user == null) {
            return null;
        }
        if (!encoder.matches(password, user.getPassword())) {
            return null;
        }

        return user;
    }
}
