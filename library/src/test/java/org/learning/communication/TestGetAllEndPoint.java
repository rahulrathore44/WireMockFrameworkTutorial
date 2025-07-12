package org.learning.communication;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.*;
import org.learning.config.Configuration;

public class TestGetAllEndPoint {

    /**
     * Before All - To Initialize all the object needed for test
     * <p>
     * Test - Test logic
     * <p>
     * <p>
     * After All - Clean up all the resource/object
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
    @DisplayName("Verify the status code is 204 when there is no data on the server")
    public void test204StatusCode() throws Exception {
        var stub = WireMock.get("/pet/all").willReturn(WireMock.noContent());
        server.stubFor(stub);
        try {
            var response = communication.getAll();
            // Validate the response is not null
            Assertions.assertNotNull(response);

            // Extract the object of type HttpResponse

            var responseData = response.returnResponse();

            // Validation on the response status code
            Assertions.assertEquals(HttpStatus.SC_NO_CONTENT, responseData.getStatusLine().getStatusCode());

            // Validate the response body is null
            Assertions.assertNull(responseData.getEntity());

        } finally {
            server.removeStub(stub);
        }

    }

    @AfterAll
    public static void tearDown() {
        if (server.isRunning())
            server.shutdownServer();

        configuration = null;
        communication = null;
    }

}
