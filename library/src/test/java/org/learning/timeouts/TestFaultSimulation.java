package org.learning.timeouts;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.UniformDistribution;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.learning.communication.Communication;
import org.learning.communication.CommunicationImpl;
import org.learning.config.Configuration;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
    @DisplayName("Verify the status code is 204 when there delay in downstream service response")
    public void test204StatusCodeWithFixedDelay() throws Exception {
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

    @Test
    @DisplayName("Verify the status code is 204 with random delay")
    public void test204StatusCodeWithRandomDelay() throws Exception {
        var stub = WireMock.get("/pet/all")
                .willReturn(WireMock
                        .noContent()
                        .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                        // stable latency 5sec +/- 2sec. 3sec, 7sec
                        .withRandomDelay(new UniformDistribution(3000, 7000))
                );
        wireMockServer.stubFor(stub);
        try {
            var response = communication.getAll();
            // Validate the response is not null
            Assertions.assertNotNull(response);

        } finally {
            wireMockServer.removeStub(stub);
        }
    }

    @Test
    @DisplayName("Verify that the server returns 200 ok status with Chunked Dribble Delay")
    public void test200OkStatusCodeWithChunkedDribbleDelay() throws Exception {

        var responseBody = """
                    {
                      "pets": [
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
                      ]
                    }
                """.trim();

        var body = new Body(responseBody);
        var stub = WireMock.get("/pet/all")
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_OK)
                        .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                        .withResponseBody(body)
                        .withChunkedDribbleDelay(6,10000)
                );
        wireMockServer.stubFor(stub);
        try {

            var response = communication.getAll();

            // Validation on response
            Assertions.assertNotNull(response);

            var responseData = response.returnResponse();

            // Validation on status code
            Assertions.assertEquals(HttpStatus.SC_OK, responseData.getStatusLine().getStatusCode());

            // Validation on response header
            var header = Arrays.stream(responseData.getHeaders(HttpHeaders.CONTENT_TYPE)).findFirst();

            if (header.isPresent()) {
                // Validation for response header
                Assertions.assertEquals(header.get().getValue(), configuration.getContentType().getMimeType());
            } else {
                Assertions.fail("Response dose not have the " + HttpHeaders.CONTENT_TYPE + " header.");
            }

            // validation on response body
            Assertions.assertNotNull(responseData.getEntity());


            try(InputStream inputStream = responseData.getEntity().getContent()) {
                var dataInBytes = inputStream.readAllBytes();
                var dataInString = new String(dataInBytes, StandardCharsets.UTF_8);
                JsonAssertions.assertThatJson(dataInString).isEqualTo(responseBody);
            }catch (Exception e){
                Assertions.fail(e.getMessage());
            }

            var dataInString = EntityUtils.toString(responseData.getEntity(),StandardCharsets.UTF_8);
            JsonAssertions.assertThatJson(dataInString).isEqualTo(responseBody);

        } finally {
            wireMockServer.removeStub(stub);
        }

    }

}
