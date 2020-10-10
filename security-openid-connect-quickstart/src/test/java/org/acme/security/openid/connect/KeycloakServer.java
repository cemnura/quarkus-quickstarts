package org.acme.security.openid.connect;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.BindMode;
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

        return Collections.singletonMap("quarkus.oidc.auth-server-url", "http://localhost:" + keycloak.getFirstMappedPort() + "/auth/realms/quarkus");
    }

    @Override
    public void stop() {
        keycloak.stop();
    }
}
