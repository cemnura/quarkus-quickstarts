package org.acme.security.openid.connect;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(KeycloakServer.class)
public class AdminResourceTest {

    private static final String KEYCLOAK_SERVER_URL = "http://localhost:8180";
    private static final String KEYCLOAK_REALM = "quarkus";

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String oidc;

    @Test
    public void testAdminAccess() {
        RestAssured.given().auth().oauth2(getAccessToken("admin"))
                .when().get("/api/admin")
                .then()
                .statusCode(200);
    }

    private String getAccessToken(String userName) {
        return RestAssured
                .given()
                .param("grant_type", "client_credentials")
                .param("username", userName)
                .param("password", userName)
                .param("client_id", "backend-service")
                .param("client_secret", "secret")
                .when()
                .post(oidc + "/protocol/openid-connect/token")
                .getBody().print();
    }

}
