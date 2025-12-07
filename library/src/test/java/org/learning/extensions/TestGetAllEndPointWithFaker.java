package org.learning.extensions;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.learning.communication.Communication;
import org.learning.communication.CommunicationImpl;
import org.learning.config.Configuration;
import org.wiremock.RandomExtension;

public class TestGetAllEndPointWithFaker {

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
        wireMockConfiguration = new WireMockConfiguration()
                .dynamicPort()
                .extensions(RandomExtension.class);
        server = new WireMockServer(wireMockConfiguration);
        server.start();
        configuration = new Configuration.ConfigurationBuilder().withUrl("http://localhost:" + server.port()).withContentType(ContentType.APPLICATION_JSON).build();
        communication = new CommunicationImpl(configuration);
    }

    @Test
    @DisplayName("Verify that the server returns 200 ok status code with repose data with Faker Extension")
    public void test200OkStatusCodeWithFakerExt() throws Exception {

        var responseBody = """
                    {
                      "pets": [
                        {
                          "id": 1,
                          "name": "{{ random 'Animal.name' }}",
                          "category": {
                            "id": 1,
                            "name": "{{ random 'Animal.species' }}"
                          },
                          "photoUrls": [
                            "{{ random 'Internet.image' }}"
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
                """.trim();

        var body = new Body(responseBody);
        var stub = WireMock.get("/pet/all")
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.SC_OK)
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                                .withResponseBody(body)
                                .withTransformers("response-template")
                );
        server.stubFor(stub);
        try {

            var response = communication.getAll();

            // Validation on response
            Assertions.assertNotNull(response);

            var responseData = response.returnResponse();


            // validation on response body
            Assertions.assertNotNull(responseData.getEntity());

            // Response
            System.out.println(EntityUtils.toString(responseData.getEntity()));

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


