package org.learning.communication.persistence;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.learning.communication.Communication;
import org.learning.communication.CommunicationImpl;
import org.learning.config.Configuration;

import java.util.Arrays;

/**
 * To run this test
 * Ensure that the system has read and write access to the location configured in the withRootDirectory() method.
 * Ensure that TestCreateEndPointWithPersistence is executed first and that stubs are persisted in the configured location.
 */
public class TestCreateEndPointWithPersistence {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer server;
    private static Configuration configuration;
    private static Communication communication;

    @BeforeAll
    public static void setUp() {
        wireMockConfiguration = new WireMockConfiguration()
                .dynamicPort()
                .withRootDirectory("C:\\Data\\Folder1");
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
                .withHeader(HttpHeaders.ACCEPT, equalTo)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalToIgnoreCase(configuration.getContentType().getMimeType()))
                .withRequestBody(WireMock.equalToJson(requestBodyInJson, true, true))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_CREATED)
                        .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                        .withResponseBody(new Body(requestBodyInJson))
                ).persistent(true).withName("StubForPost");
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
            //server.removeStub(stubForPost);
        }

    }

    @Test
    @DisplayName("Verify the 201 status code along with request and response headers With Persistence")
    public void test201CreatedWithHeaderWithPersistence() throws Exception {
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
                .withHeader(HttpHeaders.ACCEPT, equalTo)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalToIgnoreCase(configuration.getContentType().getMimeType()))
                .withRequestBody(WireMock.equalToJson(requestBodyInJson, true, true))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_CREATED)
                        .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                        .withResponseBody(new Body(requestBodyInJson))
                ).withName("StubForHeader");

        var jsonPathWithValue = new MatchesJsonPathPattern("$.tags[0]", WireMock.equalToJson("""
                {
                			"id": 1,
                			"name": "Four legs"
                		}
                """, true, true));
        var stubForPostWithJsonPath = WireMock.post("/pet")
                .withHeader(HttpHeaders.ACCEPT, equalTo)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalToIgnoreCase(configuration.getContentType().getMimeType()))
                .withRequestBody(jsonPathWithValue)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_CREATED)
                        .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                        .withResponseBody(new Body(requestBodyInJson))
                ).withName("StubWithJsonPath");

        server.stubFor(stubForPost);
        server.stubFor(stubForPostWithJsonPath);
        server.saveMappings();

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
            //server.removeStub(stubForPost);
        }

    }


}
