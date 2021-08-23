package lessons.lesson_9_spring_boot.services.users;

import lessons.lesson_9_spring_boot.dao.users.UserDao;
import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import lessons.lesson_9_spring_boot.exceptions.already_exists_exception.UserAlreadyExistsException;
import lessons.lesson_9_spring_boot.exceptions.not_found_exception.UserNotFoundException;
import lessons.lesson_9_spring_boot.exceptions.not_match_exceptions.PasswordNotMatchException;
import lessons.lesson_9_spring_boot.services.AbstractService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService implements AbstractService<UserEntity, Long> {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UserDao userDao;


    @Override
    public UserEntity findById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    public UserEntity findByUserName(String userName) {
        return userDao.findByUserName(userName);
    }

    @Override
    public List<UserEntity> findAll() {
        return userDao.findAll();
    }

    @Transactional
    @Override
    public UserEntity insert(UserEntity user) throws UserAlreadyExistsException {
        UserEntity userFromDbByEmail = userDao.findByEmail(user.getEmail());
        if (userFromDbByEmail != null) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        UserEntity userFomDbByUsername = userDao.findByUserName(user.getUserName());
        if (userFomDbByUsername != null) {
            throw new UserAlreadyExistsException("User by this username has already been registered");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user = userDao.save(user);

        return user;
    }

    @Transactional
    @Override
    public UserEntity update(UserEntity user) {
        user = userDao.save(user);

        return user;
    }

    @Transactional
    @Override
    public void delete(UserEntity user) {
        userDao.delete(user);
    }

    @Transactional
    @Override
    public void deleteAll() {
        userDao.deleteAll();
    }

    public UserEntity checkPasswordByUserName(String userName, String password)
            throws UserNotFoundException, PasswordNotMatchException {
        UserEntity userFomDb = userDao.findByUserName(userName);
        if (userFomDb == null) {
            throw new UserNotFoundException("User not found");
        }

        if (!checkPasswordCorrect(password, userFomDb.getPassword())) {
            throw new PasswordNotMatchException("Password does not correspond to this username");
        }

        return userFomDb;
    }

    private boolean checkPasswordCorrect(String passwordTry, String passwordFromDb) {
        return encoder.matches(passwordTry, passwordFromDb);
    }

}
