package org.learning;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.*;
import org.learning.communication.Communication;
import org.learning.communication.CommunicationImpl;
import org.learning.config.Configuration;

public class TestQueryAndPathParam {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer wireMockServer;
    private static Communication communication;
    private static Configuration configuration;


    @BeforeAll
    public static void setUp() {
        wireMockConfiguration = new WireMockConfiguration();
        wireMockServer = new WireMockServer(wireMockConfiguration.dynamicPort());
        wireMockServer.start();
        configuration = new Configuration.ConfigurationBuilder().withUrl("http://localhost:" + wireMockServer.port()).withContentType(ContentType.APPLICATION_JSON).build();
        communication = new CommunicationImpl(configuration);
    }

    @AfterAll
    public static void tearDown() {
        if (wireMockServer.isRunning()) {
            wireMockServer.shutdownServer();
        }
        wireMockConfiguration = null;
        configuration = null;
        communication = null;
    }

    @Test
    @DisplayName("Verify the get request with query parameters")
    public void testWithQueryParameters() throws Exception {
        var responseBody = """
                {
                  "pets": [
                    {
                      "id": 3,
                      "name": "Bruno",
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
                      "status": "sold"
                    },
                    {
                      "id": 4,
                      "name": "Bruno",
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
                      "status": "sold"
                    }
                  ]
                }
                """.stripIndent().trim();

        var stub = WireMock.get(WireMock.urlPathEqualTo("/pet/findPetsByStatus"))
                .withQueryParam("status", WireMock.equalTo("sold"))
                .withHeader(HttpHeaders.ACCEPT, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.SC_OK)
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                                .withBody(responseBody)
                );

        try {
            wireMockServer.stubFor(stub);
            var response = communication.findPetsByStatus("sold");
            Assertions.assertNotNull(response);
            var responseData = response.returnResponse();
            Assertions.assertEquals(HttpStatus.SC_OK, responseData.getStatusLine().getStatusCode());
        } finally {
            wireMockServer.removeStub(stub);
        }


    }

    @Test
    @DisplayName("Verify the get request with path parameters")
    public void testWithPathParameter() throws Exception {
        var responseBody = """
                {
                  "id": 3,
                  "name": "Bruno",
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
                  "status": "sold"
                }
                """.stripIndent().trim();

        var stub = WireMock.get(WireMock.urlPathTemplate("/pet/{petId}"))
                .withPathParam("petId", WireMock.equalTo("3"))
                .withHeader(HttpHeaders.ACCEPT, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .willReturn(
                        WireMock
                                .ok()
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                                .withBody(responseBody)
                );

        try {
            wireMockServer.stubFor(stub);
            var response = communication.findPetById("3");
            Assertions.assertNotNull(response);
            var responseData = response.returnResponse();
            Assertions.assertEquals(HttpStatus.SC_OK, responseData.getStatusLine().getStatusCode());
        } finally {
            wireMockServer.removeStub(stub);
        }

    }

}
