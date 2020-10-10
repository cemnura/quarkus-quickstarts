package org.acme.security.openid.connect;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Collections;
import java.util.Map;

public class KeycloakServer implements QuarkusTestResourceLifecycleManager {

    private GenericContainer keycloak;

    @Override
    public Map<String, String> start() {
        keycloak = new GenericContainer("quay.io/keycloak/keycloak:9.0.0")
                .withExposedPorts(8080)
                .withEnv("KEYCLOAK_PASSWORD", "admin")
                .withEnv("KEYCLOAK_IMPORT", "/tmp/realm.json")
                .withClasspathResourceMapping("quarkus-realm.json", "/tmp/realm.json", BindMode.READ_ONLY)
                .waitingFor(Wait.forHttp("/auth"));
        keycloak.start();
        System.setProperty("quarkus.oauth2.introspection-url", "http://localhost:" + keycloak.getFirstMappedPort() + "/auth/realms/quarkus/protocol/openid-connect/token/introspect");
        System.setProperty("keycloak.url", "http://localhost:" + keycloak.getFirstMappedPort() + "/auth");

        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        System.clearProperty("quarkus.oauth2.introspection-url");
        System.clearProperty("keycloak.url");
        keycloak.stop();
    }
}
