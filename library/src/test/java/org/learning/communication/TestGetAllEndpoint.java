package org.learning.communication;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.eclipse.jetty.http.HttpHeader;
import org.junit.jupiter.api.*;
import org.learning.config.Configuration;

import java.util.Arrays;

public class TestGetAllEndpoint {

    /**
     * Before All - To initialize all the required object once
     * <p>
     * Test - Actual test logic
     * <p>
     * After Call - To clean up all the object
     **/

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer server;
    private static Configuration configuration;
    private static Communication communication;

    @BeforeAll
    public static void setUp() {
        wireMockConfiguration = new WireMockConfiguration().dynamicPort();
        server = new WireMockServer(wireMockConfiguration);
        server.start();
        configuration = new Configuration.ConfigurationBuilder().withUrl("http://localhost:" + server.port()).withContentType(ContentType.APPLICATION_JSON).build();
        communication = new CommunicationImpl(configuration);
    }


    @Test
    @DisplayName("Verify when server has not data then 204 status code should be returner")
    public void testForNoContent() throws Exception {
        var stub = WireMock.get("/pet/all").willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_NO_CONTENT).withHeader(HttpHeader.CONTENT_TYPE.asString(), ContentType.APPLICATION_JSON.getMimeType()));
        server.stubFor(stub);
        try {
            var response = communication.getAll();
            Assertions.assertNotNull(response);
            // Validation on status code
            var httpResponse = response.returnResponse();
            Assertions.assertEquals(HttpStatus.SC_NO_CONTENT, httpResponse.getStatusLine().getStatusCode());
            // Validation on Response Body
            Assertions.assertNull(httpResponse.getEntity());
            // Validation on Response headers
            var header = Arrays.stream(httpResponse.getAllHeaders()).anyMatch((x) -> x.getName().equals(HttpHeader.CONTENT_TYPE.asString()));
            Assertions.assertTrue(header, HttpHeader.CONTENT_TYPE.asString() + " is not present in response ");
        } finally {
            server.removeStub(stub);
        }
    }


    @AfterAll
    public static void tearDown() {
        if (server.isRunning()) {
            server.shutdownServer();
        }
        configuration = null;
        communication = null;
    }

}











