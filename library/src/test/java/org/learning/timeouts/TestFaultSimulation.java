package org.learning.timeouts;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.*;
import org.learning.communication.Communication;
import org.learning.communication.CommunicationImpl;
import org.learning.config.Configuration;

import java.net.SocketTimeoutException;

public class TestFaultSimulation {

    public static WireMockConfiguration wireMockConfiguration;
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
                .withConnectionTimeOut(9000)
                .withSocketTimeOut(10000)
                .build();
        communication = new CommunicationImpl(configuration);
    }


    @AfterAll
    public static void tearDown() {
        if (wireMockServer.isRunning())
            wireMockServer.shutdown();
        wireMockConfiguration = null;
        communication = null;
        configuration = null;
    }


    @Test
    @DisplayName("Verify the status code is 204 when there is no data on the server")
    public void test204StatusCode() throws Exception {
        var stub = WireMock.get("/pet/all")
                .willReturn(WireMock
                        .noContent()
                        .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                        .withFixedDelay(30000)
                );
        wireMockServer.stubFor(stub);
        Assertions.assertThrows(SocketTimeoutException.class, () -> {
            try {
                var response = communication.getAll();
                // Validate the response is not null
                Assertions.assertNotNull(response);

            } finally {
                wireMockServer.removeStub(stub);
            }
        });

    }

}
