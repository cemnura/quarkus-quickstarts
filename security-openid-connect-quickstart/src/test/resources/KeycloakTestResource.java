import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class KeycloakTestResource implements QuarkusTestResourceLifecycleManager {

    private WireMockServer server;

    @Override
    public Map<String, String> start() {

        server = new WireMockServer(options().port(8180));
        server.start();

        WireMock.configureFor(8180);
        WireMock.stubFor(
                get(urlEqualTo("/auth/realms/quarkus/.well-known/openid-configuration"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                                .withBody("{\n" +
                                        "    \"authorization_endpoint\": \"http://localhost:8180/authenticate\",\n" +
                                        "    \"end_session_endpoint\": \"http://localhost:8180/logout\",\n" +
                                        "    \"id_token_signing_alg_values_supported\": [\n" +
                                        "        \"RS256\",\n" +
                                        "        \"ES256\",\n" +
                                        "        \"HS256\"\n" +
                                        "    ],\n" +
                                        "    \"issuer\": \"http://localhost:8180/auth/realms/quarkus\",\n" +
                                        "    \"jwks_uri\": \"http://localhost:8180/auth/realms/quarkus/protocol/openid-connect/certs\",\n" +
                                        "    \"response_types_supported\": [\n" +
                                        "        \"code\",\n" +
                                        "        \"code id_token\",\n" +
                                        "        \"id_token\",\n" +
                                        "        \"token id_token\"\n" +
                                        "    ],\n" +
                                        "    \"subject_types_supported\": [\n" +
                                        "        \"public\"\n" +
                                        "    ],\n" +
                                        "    \"token_endpoint\": \"http://localhost:8180/auth/realms/quarkus/protocol/openid-connect/token\"\n" +
                                        "}")));

        WireMock.stubFor(
                get(urlEqualTo("/auth/realms/quarkus/protocol/openid-connect/certs"))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                            .withBody("{\n" +
                                        "    \"keys\": [\n" +
                                        "        {\n" +
                                        "            \"alg\": \"RS256\",\n" +
                                        "            \"e\": \"AQAB\",\n" +
                                        "            \"kid\": \"keyId\",\n" +
                                        "            \"kty\": \"RSA\",\n" +
                                        "            \"n\": \"AKzaf4nijuwtAn9ieZaz-iGXBp1pFm6dJMAxRO6ax2CV9cBFeThxrKJNFmDY7j7gKRnrgWxvgJKSd3hAm_CGmXHbTM8cPi_gsof-CsOohv7LH0UYbr0UpCIJncTiRrKQto7q_NOO4Jh1EBSLMPX7MzttEhh35Ue9txHLq3zkdkR6BR6nGS7QxEg7FzYzA4IooV59OPr-TvlDxbEpwc1wkRZDGavo-WjngAt7m_BEQtHnav3whitbrMmi_1tWY8cQbO9D4FuQTM7yvACLSv94G2TCvsjm_gGJmOJyRBkI1r-uEIfhz9-VIKlswqapKSul-Hoxv5NycucRa4xi4N39dfM=\",\n" +
                                        "            \"use\": \"sig\"\n" +
                                        "        }\n" +
                                        "    ]\n" +
                                        "}"))
        );

        WireMock.stubFor(
                post(urlEqualTo("/realms/quarkus/protocol/openid-connect/token"))
                .willReturn(aResponse()
                            .withBody("eyJraWQiOiJrZXlJZCIsImFsZyI6IlJTMjU2In0.eyJhdWQiOlsiYXVkaWVuY2UiXSwiaWF0IjoxNjAyMjc3MzI5LCJhdXRoX3RpbWUiOjAsImV4cCI6MTYwMjI3Nzk4OSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDoxMjM0L2F1dGgvcmVhbG1zL2N1c3RvbSIsInN1YiI6InN1YmplY3QiLCJzY29wZSI6Im9wZW5pZCBzY29wZSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImF1dGhvcml6ZWRfcGFydHkiLCJuYmYiOjE2MDIyNzczODksIm5hbWUiOiJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImdpdmVuIiwiZmFtaWx5X25hbWUiOiJmYW1pbHkiLCJlbWFpbCI6ImVtYWlsIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidXNlcm5hbWUiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiYWRtaW4iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhdWRpZW5jZSI6eyJyb2xlcyI6WyJhZG1pbiJdfSwiYXV0aG9yaXplZF9wYXJ0eSI6eyJyb2xlcyI6WyJwYXJ0eSJdfX0sImNsYWltIjoibXkgY2xhaW0iLCJpc3N1ZXIiOiJpZ25vcmVkIn0.mzPB05nrMoJqdyzXv1q4ZLjg8i_OecJLeO0QSNtwMyYSE_RHfrxkTXMQiobgVsF7zp8ik2ynmfovQxBmOQcRVHkznjbSc4h2YEBOwRxnefOzk8fxWoDejJ8tgR__Xyo0uNwNnx1HOadWZWXbjGbYUOmDPibkv8PcjaB1HD19AAwKEmgOtAj-wJtWfOZZ756D67SdSjtyitXB-ZTXeTbi1Fh3cEPTMJz53PdOz-47NvLydijCtkiuTzwnh0eSiIPDi3qTN7bgQD5OdHXu2APAgOhf9JVXgL6srM7eIT9hq9K2dp7ObG3UQNXCO4RH1md74-_eEhXZeYxVYD9JFM1f7Q")
                ));

        System.out.println("[INFO] Keycloak started in mock mode; server status");
        return Collections.singletonMap("quarkus.oidc.auth-server-url", server.baseUrl() + "/auth/realms/quarkus");
    }

    @Override
    public synchronized void stop() {
        if (server != null) {
            server.stop();
            System.out.println("[INFO] Keycloak was shut down");
            server = null;
        }
    }

}
