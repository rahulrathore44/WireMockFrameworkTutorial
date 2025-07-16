package org.learning.communication;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.learning.config.Configuration;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
        var stub = WireMock.get("/pet/all").willReturn(WireMock.noContent().withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType()));
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

            var header = Arrays.stream(responseData.getHeaders(HttpHeaders.CONTENT_TYPE)).findFirst();

            if (header.isPresent()) {
                // Validation for response header
                Assertions.assertEquals(header.get().getValue(), configuration.getContentType().getMimeType());
            } else {
                Assertions.fail("Response dose not have the " + HttpHeaders.CONTENT_TYPE + " header.");
            }

        } finally {
            server.removeStub(stub);
        }

    }

    @Test
    @DisplayName("Verify that the server returns 200 ok status code with repose data")
    public void test200OkStatusCode() throws Exception {

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
        var stub = WireMock.get("/pet/all").willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_OK).withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType()).withResponseBody(body));
        server.stubFor(stub);
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
