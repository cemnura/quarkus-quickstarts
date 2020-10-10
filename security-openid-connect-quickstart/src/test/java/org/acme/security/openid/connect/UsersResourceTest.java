package org.acme.security.openid.connect;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(KeycloakServer.class)
public class UsersResourceTest {

    private static final String KEYCLOAK_SERVER_URL = "http://localhost:8180";
    private static final String KEYCLOAK_REALM = "quarkus";

    @Test
    public void testUserAccess() {
        RestAssured.given().auth().oauth2(getAccessToken("jdoe"))
                .when().get("/api/users")
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
                .param("grant_type", "client_credentials")
                .param("username", userName)
                .param("password", userName)
                .param("client_id", "backend-service")
                .param("client_secret", "secret")
                .when()
                .post(KEYCLOAK_SERVER_URL + "/realms/" + KEYCLOAK_REALM + "/protocol/openid-connect/token")
                .jsonPath().get("access_token");
    }

}
