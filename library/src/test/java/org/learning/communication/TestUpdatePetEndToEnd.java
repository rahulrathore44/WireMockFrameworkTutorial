package org.learning.communication;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.*;
import org.learning.config.Configuration;

public class TestUpdatePetEndToEnd {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer wireMockServer;
    private static Communication communication;
    private static Configuration configuration;
    private static final String PET_LIFE_CYCLE = "Pet Life Cycle";


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
        wireMockServer = null;
        communication = null;
        configuration = null;
    }

    @Test
    @DisplayName("Verify the Pet life cycle using scenario - state full behaviour")
    public void testPetObjectLifecycle() throws Exception {

        var body = """
                {
                	"id": 1,
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
                  "status": "sold"
                }
                """.stripIndent().trim();

        var petCreated = WireMock.post("/pet")
                .inScenario(PET_LIFE_CYCLE)
                .whenScenarioStateIs(Scenario.STARTED)
                .withHeader(HttpHeaders.ACCEPT, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .withRequestBody(WireMock.equalToJson(body, true, true))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.SC_CREATED)
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                                .withBody(body)
                )
                .willSetStateTo("Pet Created");

        var getPet = WireMock.get(WireMock.urlPathTemplate("/pet/{petId}"))
                .inScenario(PET_LIFE_CYCLE)
                .whenScenarioStateIs("Pet Created")
                .withPathParam("petId", WireMock.equalTo("1"))
                .withHeader(HttpHeaders.ACCEPT, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.SC_OK)
                                .withBody(body)
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                );

        var updatePet = WireMock.patch(WireMock.urlPathTemplate("/pet/{petId}"))
                .inScenario(PET_LIFE_CYCLE)
                .whenScenarioStateIs("Pet Created")
                .willSetStateTo("Pet Updated")
                .withPathParam("petId", WireMock.equalTo("1"))
                .withHeader(HttpHeaders.ACCEPT, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .withRequestBody(WireMock.equalToJson(updateBody, true, true))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.SC_OK)
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                                .withBody(updateBody)
                );

        var getUpdatedPet = WireMock.get(WireMock.urlPathTemplate("/pet/{petId}"))
                .inScenario(PET_LIFE_CYCLE)
                .whenScenarioStateIs("Pet Updated")
                .withPathParam("petId", WireMock.equalTo("1"))
                .withHeader(HttpHeaders.ACCEPT, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.SC_OK)
                                .withBody(updateBody)
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                );

        try {
            wireMockServer.stubFor(petCreated);
            wireMockServer.stubFor(getPet);
            wireMockServer.stubFor(updatePet);
            wireMockServer.stubFor(getUpdatedPet);

            /**

             //var response = communication.create(body);
             var response = communication.findPetById("1");
             //response = communication.updatePetById("1", updateBody);
             //response = communication.findPetById("1");
             **/

            var response = communication.create(body);
            response = communication.findPetById("1");
            response = communication.updatePetById("1", updateBody);
            response = communication.findPetById("1");

            Assertions.assertNotNull(response);

            var responseData = response.returnResponse();

            Assertions.assertEquals(HttpStatus.SC_OK, responseData.getStatusLine().getStatusCode());


        } finally {
            wireMockServer.removeStub(petCreated);
            wireMockServer.removeStub(getPet);
            wireMockServer.removeStub(updatePet);
            wireMockServer.removeStub(getUpdatedPet);
        }

    }

}
