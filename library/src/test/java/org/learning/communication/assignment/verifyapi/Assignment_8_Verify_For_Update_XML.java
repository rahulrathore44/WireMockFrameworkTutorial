package org.learning.communication.assignment.verifyapi;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.communication.Communication;
import org.learning.communication.CommunicationImpl;
import org.learning.config.Configuration;

public class Assignment_8_Verify_For_Update_XML {

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

        communication = null;
        configuration = null;
        wireMockConfiguration = null;

    }

    @DisplayName("Verify the Patch end point of the application using wiremock")
    @Test
    public void testPatchRequestWithStatus200WithVerify() throws Exception {
        var updateBody = """
                {
                  "id": 1,
                  "name": "Bruno-Updated",
                  "category": {
                    "id": 1,
                    "name": "Dog"
                  },
                  "photoUrls": [
                    "http://localhost:8909/pics/dog.jpg"
                  ],
                  "tags": [
                    {
                      "id": 1,
                      "name": "Four legs"
                    }
                  ],
                  "status": "available"
                }
                """.stripIndent().trim();

        var stub = WireMock.patch(WireMock.urlPathTemplate("/pet/{petId}"))
                .withPathParam("petId", WireMock.equalTo("1"))
                .withHeader(HttpHeaders.ACCEPT, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .withRequestBody(WireMock.equalToJson(updateBody, true, true))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(200)
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                                .withBody(updateBody)
                );

        try {
            wireMockServer.stubFor(stub);
            communication.updatePetById("1", updateBody);
            wireMockServer.verify(WireMock
                    .patchRequestedFor(WireMock.urlPathTemplate("/pet/{petId}"))
                    .withPathParam("petId", WireMock.equalTo("1"))
            );
        } finally {
            wireMockServer.removeStub(stub);
        }

    }
}
