package com.chalova.irina.myfinances.users_service;

import com.chalova.irina.myfinances.users_service.entities.UserEntity;
import com.chalova.irina.myfinances.users_service.exceptions.UserNotFoundException;
import com.chalova.irina.myfinances.users_service.services.UserService;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator,
        CredentialInputUpdater,
        UserQueryProvider,
        UserRegistrationProvider {
    private final Logger log = LoggerFactory.getLogger(CustomUserStorageProvider.class);
    private final KeycloakSession ksession;
    private final ComponentModel model;
    private final UserService userService;

    public CustomUserStorageProvider(KeycloakSession ksession, ComponentModel model,
                                     UserService userService) {
        this.ksession = ksession;
        this.model = model;
        this.userService = userService;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;
        UserEntity userEntity = userService.findByUserName(user.getUsername(), model);
        if (userEntity == null) {
            return false;
        }
        try {
            userService.updatePasswordById(Long.parseLong(user.getId()),
                    cred.getChallengeResponse(), model);
        } catch (UserNotFoundException e) {
            return false;
        }

        return true;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {

    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return Collections.emptySet();
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        if (!supportsCredentialType(credentialInput.getType()) || !(credentialInput instanceof UserCredentialModel)) {
            return false;
        }

        UserCredentialModel cred = (UserCredentialModel) credentialInput;
        boolean validateCredentials = false;
        try {
            log.info("Checking user credentials...");
            validateCredentials =
                    userService.checkPasswordByUserName(
                            user.getUsername(),
                            cred.getChallengeResponse(),
                            model
                    );
        } catch (UserNotFoundException e) {
            return false;
        }
        log.info("user successfully verified");

        return validateCredentials;
    }

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        String externalId = StorageId.externalId(id);

        return new UserAdapter(ksession, realm, model,
                userService.findById(Long.parseLong(externalId), model), userService);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        log.info("[I41] getUserByUsername({})", username);

        UserEntity user = userService.findByUserName(username, model);
        if (user != null) {
            log.info("getUserByUsername: user != null");
            return new UserAdapter(ksession, realm, model, user, userService);
        }

        return null;
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        UserEntity user = userService.findByEmail(email, model);
        if (user != null) {
            return new UserAdapter(ksession, realm, model, user, userService);
        }

        return null;
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return userService.getUsersCount(model);
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        return userService.findAll(model).stream()
                .map(user -> new UserAdapter(ksession, realm, model, user, userService))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        return getUsers(realm);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        return userService.findAll(model).stream()
                .filter(user -> user.getUserName().contains(search) || user.getEmail().contains(search))
                .map(user -> new UserAdapter(ksession, realm, model, user, userService))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        return searchForUser(search, realm);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        return userService.findAll(model).stream()
//                .filter(user -> user.getFirstName().contains(params.get(UserModel.FIRST_NAME)) ||
//                        user.getLastName().contains(params.get(UserModel.LAST_NAME)) ||
//                        user.getUserName().contains(params.get(UserModel.USERNAME)) ||
//                        user.getEmail().contains(params.get(UserModel.EMAIL)) ||
//                        String.valueOf(user.getAge()).equals(params.get("age")))
                .map(user -> new UserAdapter(ksession, realm, model, user, userService))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        return searchForUser(params, realm);
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        return Collections.emptyList();
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        return null;
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
//        try {
//            userService.deleteById(Long.parseLong(user.getId()));
//        } catch (UserNotFoundException | OperationFailedException e) {
//            return false;
//        }
//
//        return true;
        return false;
    }

}
