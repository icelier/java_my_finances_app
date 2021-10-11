package com.chalova.irina.myfinances.users_service;

import com.chalova.irina.myfinances.users_service.entities.UserEntity;
import com.chalova.irina.myfinances.users_service.exceptions.UserNotFoundException;
import com.chalova.irina.myfinances.users_service.services.UserService;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.*;

public class UserAdapter extends AbstractUserAdapterFederatedStorage {
    private final String keycloakId;

    private UserEntity user;
    private final UserService userService;
    private final ComponentModel model;
    private final Set<RoleModel> userRoles = new HashSet<>();

    public UserAdapter(KeycloakSession session, RealmModel realm,
                       ComponentModel model, UserEntity user, UserService userService) {
        super(session, realm, model);
        this.model = model;
        this.userService = userService;
        this.storageId = new StorageId(storageProviderModel.getId(), user.getId().toString());
        this.keycloakId = StorageId.keycloakId(model, user.getId().toString());
        this.user = user;
        user.getRoles().forEach(role -> userRoles.add(new UserRoleModel(role, realm)));
    }

    @Override
    protected Set<RoleModel> getRoleMappingsInternal() {
        return userRoles;
    }

    @Override
    public String getId() {
        return keycloakId;
    }

    public Long getExternalId() {
        return Long.parseLong(storageId.getExternalId());
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public void setUsername(String username) {
        user.setUserName(username);
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public void setEmail(String email) {
        user.setEmail(email);
    }

    @Override
    public String getFirstName() {
        return user.getFirstName();
    }

    @Override
    public void setFirstName(String firstName) {
        user.setFirstName(firstName);
    }

    @Override
    public String getLastName() {
        return user.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        user.setLastName(lastName);
    }

    @Override
    protected Set<RoleModel> getFederatedRoleMappings() {
        return userRoles;
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        if (name.equals("age")) {
            user.setAge(Integer.parseInt(value));
        } else {
            super.setSingleAttribute(name, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        if (name.equals("age")) {
            user.setAge(0);
        } else {
            super.removeAttribute(name);
        }
        try {
            user = userService.update(user.getId(), user, model);
        } catch (UserNotFoundException ignored) {
        }
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        if (name.equals("age")) {
            user.setAge(Integer.parseInt(values.get(0)));
        } else {
            super.setAttribute(name, values);
        }
        try {
            user = userService.update(user.getId(), user, model);
        } catch (UserNotFoundException ignored) {
        }
    }

    @Override
    public String getFirstAttribute(String name) {
        if (name.equals("age")) {
            return String.valueOf(user.getAge());
        } else {
            return super.getFirstAttribute(name);
        }
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attrs = super.getAttributes();
        MultivaluedHashMap<String, String> all = new MultivaluedHashMap<>();
        all.putAll(attrs);
        all.add("age", String.valueOf(user.getAge()));

        return all;
    }

    @Override
    public List<String> getAttribute(String name) {
        if (name.equals("age")) {
            List<String> age = new LinkedList<>();
            age.add(String.valueOf(user.getAge()));

            return age;
        } else {
            return super.getAttribute(name);
        }
    }


}
