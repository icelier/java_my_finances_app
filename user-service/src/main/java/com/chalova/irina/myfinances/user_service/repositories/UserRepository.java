package com.chalova.irina.myfinances.user_service.repositories;

import com.chalova.irina.myfinances.user_service.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findByUserName(String username);
}
