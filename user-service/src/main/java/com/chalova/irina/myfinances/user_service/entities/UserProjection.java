package com.chalova.irina.myfinances.user_service.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "productProjection", types = UserEntity.class)
public interface UserProjection {

}
