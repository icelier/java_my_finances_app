package lessons.lesson_9_spring_boot.controllers.users.login;

import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToLoginResponseConverter implements Converter<UserEntity, LoginResponse> {
    @Override
    public LoginResponse convert(UserEntity userEntity) {

        return new LoginResponse(
                userEntity.getUserName(),
                userEntity.getFullName(),
                userEntity.getAge(),
                userEntity.getEmail(),
                userEntity.getAccounts()
        );
    }
}
