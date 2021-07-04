package com.chalova.irina.myfinances.user_service.repositories;

import com.chalova.irina.myfinances.user_service.entities.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByName(String name);
}
