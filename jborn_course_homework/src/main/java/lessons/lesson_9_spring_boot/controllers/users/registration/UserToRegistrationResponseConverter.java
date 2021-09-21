package lessons.lesson_9_spring_boot.controllers.users.registration;

import lessons.lesson_9_spring_boot.entities.users.UserEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToRegistrationResponseConverter implements Converter<UserEntity, RegistrationResponse> {
    @Override
    public RegistrationResponse convert(UserEntity userEntity) {
        return new RegistrationResponse(userEntity.getId(),
                userEntity.getUserName(),
                userEntity.getFullName(),
                userEntity.getAge(),
                userEntity.getEmail(),
                userEntity.getAccounts(),
                userEntity.getRoles());
    }
}
