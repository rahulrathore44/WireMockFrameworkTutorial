package org.learning.communication;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.*;
import org.learning.config.Configuration;

public class TestDeleteEndPoint {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer wireMockServer;
    private static Communication communication;
    private static Configuration configuration;

    @BeforeAll
    public static void setUp() {
        wireMockConfiguration = new WireMockConfiguration();
        wireMockServer = new WireMockServer(wireMockConfiguration.dynamicPort());
        wireMockServer.start();
        configuration = new Configuration.ConfigurationBuilder()
                .withUrl("http://localhost:" + wireMockServer.port())
                .withContentType(ContentType.APPLICATION_JSON)
                .build();
        communication = new CommunicationImpl(configuration);
    }

    @AfterAll
    public static void tearDown() {
        if (wireMockServer.isRunning())
            wireMockServer.shutdownServer();

        wireMockConfiguration = null;
        communication = null;
        configuration = null;
    }

    @Test
    @DisplayName("Verify the delete request with Basic Auth")
    public void testDeleteWithBasicAuth() throws Exception {

        var stubForDelete = WireMock.delete(WireMock.urlPathTemplate("/pet/{petId}"))
                .withPathParam("petId", WireMock.equalTo("3"))
                // use admin & welcome
                .withBasicAuth("", "")
                .willReturn(
                        WireMock.ok()
                ).atPriority(1);

        var stubForDelete401 = WireMock.delete(WireMock.urlPathTemplate("/pet/{petId}"))
                .withPathParam("petId", WireMock.equalTo("3"))
                .willReturn(
                        WireMock.unauthorized()
                ).atPriority(2);

        try {
            wireMockServer.stubFor(stubForDelete);
            wireMockServer.stubFor(stubForDelete401);
            // use admin & welcome
            var response = communication.deletePetById("3", "", "");
            Assertions.assertNotNull(response);
            Assertions.assertEquals(HttpStatus.SC_OK, response.returnResponse().getStatusLine().getStatusCode());
            response = communication.deletePetById("3", null, null);
            Assertions.assertEquals(HttpStatus.SC_UNAUTHORIZED, response.returnResponse().getStatusLine().getStatusCode());
        } finally {
            wireMockServer.resetMappings();
        }

    }
}
