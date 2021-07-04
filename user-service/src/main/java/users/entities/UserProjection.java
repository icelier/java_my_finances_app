package users.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "productProjection", types = UserEntity.class)
public interface UserProjection {

}
