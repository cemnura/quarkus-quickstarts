package org.acme.security.openid.connect;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(KeycloakServer.class)
public class UsersResourceTest {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String oidc;

    @Test
    public void testUserAccess() {

        RestAssured.given().auth().oauth2(getAccessToken("alice"))
                .when().get("/api/users/me")
                .then()
                .statusCode(200);

        RestAssured.given().auth().oauth2(getAccessToken("admin"))
                .when().get("/api/users/me")
                .then()
                .statusCode(200);
    }

//    @Test
//    public void testWriterReaderAccess() {
//        RestAssured.given().auth().oauth2(getAccessToken("alice"))
//                .when().get("/secured/roles-allowed")
//                .then()
//                .statusCode(200);
//        RestAssured.given().auth().oauth2(getAccessToken("jdoe"))
//                .when().get("/secured/roles-allowed")
//                .then()
//                .statusCode(200);
//        RestAssured.given()
//                .when().get("/secured/roles-allowed")
//                .then()
//                .statusCode(401);
//    }

    private String getAccessToken(String userName) {
        return RestAssured
                .given()
                .param("grant_type", "password")
                .param("username", userName)
                .param("password", userName)
                .param("client_id", "backend-service")
                .param("client_secret", "secret")
                .when()
                .post(oidc + "/protocol/openid-connect/token")
                .jsonPath().get("access_token");
    }

}
