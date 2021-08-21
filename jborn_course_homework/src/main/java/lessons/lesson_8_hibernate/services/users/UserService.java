package lessons.lesson_8_hibernate.services.users;

import lessons.lesson_8_hibernate.dao.users.UserDao;
import lessons.lesson_8_hibernate.entities.users.UserEntity;
import lessons.lesson_8_hibernate.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.DataNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_8_hibernate.exceptions.not_match_exceptions.PasswordNotMatchException;
import lessons.lesson_8_hibernate.exceptions.operation_failed.OperationFailedException;
import lessons.lesson_8_hibernate.services.AbstractService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends AbstractService<UserEntity, Long> {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserEntity findById(Long id) throws OperationFailedException {
        return userDao.findById(id);
    }

    @Override
    public List<UserEntity> findAll() throws OperationFailedException {
        return userDao.findAll();
    }

    @Override
    public UserEntity insert(UserEntity user) throws UserAlreadyExistsException, OperationFailedException {
        UserEntity userFromDbByEmail;
        try {
            userFromDbByEmail = userDao.findByEmail(user.getEmail());
            if (userFromDbByEmail != null) {
                throw new UserAlreadyExistsException("Email already registered");
            }
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        UserEntity userFomDbByUsername;
        try {
            userFomDbByUsername = userDao.findByUserName(user.getUserName());
            if (userFomDbByUsername != null) {
                throw new UserAlreadyExistsException("User by this username has already been registered");
            }
        }   catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user = userDao.insert(user);

        return user;
    }

    @Override
    public UserEntity update(UserEntity user) throws UserNotFoundException, OperationFailedException {
        String password = user.getPassword();
        user.setPassword(encoder.encode(password));

        return userDao.update(user);
    }

    @Override
    public void delete(UserEntity user) throws OperationFailedException, DataNotFoundException {
        userDao.delete(user);
    }

    @Override
    public int deleteAll() throws OperationFailedException {
        return userDao.deleteAll();
    }

    public UserEntity findByUserNameAndPassword(String name, String password) throws UserNotFoundException, PasswordNotMatchException, OperationFailedException {
        UserEntity user = userDao.findByUserName(name);

        if (!checkPasswordCorrect(password, user)) {
            throw new PasswordNotMatchException("Password does not correspond to this username");
        }

        return user;
    }

    public UserEntity findByUserName(String name) throws UserNotFoundException, OperationFailedException {
        return userDao.findByUserName(name);
    }

    private boolean checkPasswordCorrect(String passwordTry, UserEntity originUserEntity) {
        return encoder.matches(passwordTry, originUserEntity.getPassword());
    }

}
