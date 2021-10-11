package com.chalova.irina.myfinances.users_service;

import com.chalova.irina.myfinances.users_service.dao.RoleRepository;
import com.chalova.irina.myfinances.users_service.dao.UserRepository;
import com.chalova.irina.myfinances.users_service.services.UserService;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.util.List;

import static com.chalova.irina.myfinances.users_service.CustomUserStorageProviderConstants.*;

public class CustomUserStorageProviderFactory
        implements UserStorageProviderFactory<CustomUserStorageProvider> {
    private static final Logger log = LoggerFactory.getLogger(CustomUserStorageProviderFactory.class);
    protected final List<ProviderConfigProperty> configMetadata;

    public CustomUserStorageProviderFactory() {
        log.info("[I24] CustomUserStorageProviderFactory created");


        // Create config metadata
        configMetadata = ProviderConfigurationBuilder.create()
                .property()
                .name(CONFIG_KEY_JDBC_DRIVER)
                .label("JDBC Driver Class")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("org.postgresql.Driver")
                .helpText("Fully qualified class name of the JDBC driver")
                .add()
                .property()
                .name(CONFIG_KEY_JDBC_URL)
                .label("JDBC URL")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("jdbc:postgresql://localhost:5432/postgres?currentSchema=finances")
                .helpText("JDBC URL used to connect to the user database")
                .add()
                .property()
                .name(CONFIG_KEY_DB_USERNAME)
                .label("Database User")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("postgres")
                .helpText("Username used to connect to the database")
                .add()
                .property()
                .name(CONFIG_KEY_DB_PASSWORD)
                .label("Database Password")
                .type(ProviderConfigProperty.STRING_TYPE)
                .helpText("Password used to connect to the database")
                .defaultValue("clai531_Tre")
                .secret(true)
                .add()
                .property()
                .name(CONFIG_KEY_VALIDATION_QUERY)
                .label("SQL Validation Query")
                .type(ProviderConfigProperty.STRING_TYPE)
                .helpText("SQL query used to validate a connection")
                .defaultValue("select 1")
                .add()
                .build();

    }

    @Override
    public String getId() {
        return "custom-user-provider";
    }

    @Override
    public CustomUserStorageProvider create(KeycloakSession ksession, ComponentModel model) {
        UserService userService = new UserService(new UserRepository(), new RoleRepository());

        return new CustomUserStorageProvider(ksession, model, userService);
    }

    // Configuration support methods
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configMetadata;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {

        try (Connection c = DbUtil.getConnection(config)) {
            log.info("[I84] Testing connection..." );
            c.createStatement().execute(config.get(CONFIG_KEY_VALIDATION_QUERY));
            log.info("[I92] Connection OK !" );
        }
        catch(Exception ex) {
            log.warn("[W94] Unable to validate connection: ex={}", ex.getMessage());
            throw new ComponentValidationException("Unable to validate database connection",ex);
        }
    }

    @Override
    public void onUpdate(KeycloakSession session, RealmModel realm, ComponentModel oldModel, ComponentModel newModel) {
        log.info("[I94] onUpdate()" );
    }

    @Override
    public void onCreate(KeycloakSession session, RealmModel realm, ComponentModel model) {
        log.info("[I99] onCreate()" );
    }
}
