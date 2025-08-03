package org.learning.communication;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.learning.config.Configuration;
import org.learning.matchers.EndsWithPattern;

import java.util.Arrays;

public class TestCreateEndPoint {

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

    @AfterAll
    public static void tearDown() {
        if (server.isRunning())
            server.shutdownServer();

        configuration = null;
        communication = null;
    }

    @Test
    @DisplayName("Verify that 201 status code is returned from server for post call")
    public void test201StatusCode() throws Exception {
        var requestBodyInJson = """
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

        // var equalToJson = new EqualToJsonPattern(requestBodyInJson, true, true);
        //var notEqualTo = new NotPattern(equalToJson);
        var stubForPost = WireMock.post("/pet")
                .withRequestBody(WireMock.equalToJson(requestBodyInJson, true, true))
                //.withRequestBody(notEqualTo)
                //.withRequestBody(WireMock.not(WireMock.equalToJson(requestBodyInJson, true, true)))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_CREATED)
                        .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                        .withResponseBody(new Body(requestBodyInJson))
                );
        server.stubFor(stubForPost);
        try {

            var response = communication.create(requestBodyInJson);

            // Validation for response not null
            Assertions.assertNotNull(response);

            // Validation on response status code

            // Validation on response headers

            // Validation on response body

        } finally {
            server.removeStub(stubForPost);

        }

    }

    @Test
    @DisplayName("Verify the 201 status code along with request and response headers")
    public void test201CreatedWithHeader() throws Exception {
        var requestBodyInJson = """
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

        var equalTo = new EqualToPattern(configuration.getContentType().getMimeType(), true);
        var stubForPost = WireMock.post("/pet")
                //.withHeader(HttpHeaders.ACCEPT, WireMock.not(equalTo))
                .withHeader(HttpHeaders.ACCEPT, equalTo)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalToIgnoreCase(configuration.getContentType().getMimeType()))
                .withRequestBody(WireMock.equalToJson(requestBodyInJson, true, true))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_CREATED)
                        .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                        .withResponseBody(new Body(requestBodyInJson))
                );
        server.stubFor(stubForPost);
        try {

            var response = communication.create(requestBodyInJson);

            // Validation for response not null
            Assertions.assertNotNull(response);

            // Validation on response status code
            var responseData = response.returnResponse();
            Assertions.assertEquals(HttpStatus.SC_CREATED, responseData.getStatusLine().getStatusCode());

            // Validation on response headers
            var header = Arrays.stream(responseData.getHeaders(HttpHeaders.CONTENT_TYPE)).findFirst();
            if (header.isPresent()) {
                Assertions.assertEquals(configuration.getContentType().getMimeType(), header.get().getValue());
            } else {
                Assertions.fail("Content Type header is not present in the response");
            }

            // Validation on response body
            var actualResponseBody = EntityUtils.toString(responseData.getEntity());
            Assertions.assertEquals(requestBodyInJson, actualResponseBody);

        } finally {
            server.removeStub(stubForPost);

        }

    }

    @Test
    @DisplayName("Verify the 201 status code with custom matcher")
    public void test201CreatedWithCustomMatcher() throws Exception {

        var requestBodyInJson = """
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

        var equalTo = new EqualToPattern(configuration.getContentType().getMimeType(), true);
        var endsWith = new EndsWithPattern(configuration.getContentType().getMimeType(), "json");
        var stubForPost = WireMock.post("/pet")
                //.withHeader(HttpHeaders.ACCEPT, WireMock.not(equalTo))
                .withHeader(HttpHeaders.ACCEPT, equalTo)
                //.withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalToIgnoreCase(configuration.getContentType().getMimeType()))
                //.withHeader(HttpHeaders.CONTENT_TYPE, endsWith)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.including(WireMock.equalToIgnoreCase(configuration.getContentType().getMimeType()), endsWith))
                .withRequestBody(WireMock.equalToJson(requestBodyInJson, true, true))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_CREATED)
                        .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                        .withResponseBody(new Body(requestBodyInJson))
                );
        server.stubFor(stubForPost);
        try {

            var response = communication.create(requestBodyInJson);

            // Validation for response not null
            Assertions.assertNotNull(response);

            // Validation on response status code
            var responseData = response.returnResponse();
            Assertions.assertEquals(HttpStatus.SC_CREATED, responseData.getStatusLine().getStatusCode());

            // Validation on response headers
            var header = Arrays.stream(responseData.getHeaders(HttpHeaders.CONTENT_TYPE)).findFirst();
            if (header.isPresent()) {
                Assertions.assertEquals(configuration.getContentType().getMimeType(), header.get().getValue());
            } else {
                Assertions.fail("Content Type header is not present in the response");
            }

            // Validation on response body
            var actualResponseBody = EntityUtils.toString(responseData.getEntity());
            Assertions.assertEquals(requestBodyInJson, actualResponseBody);

        } finally {
            server.removeStub(stubForPost);

        }

    }

}
