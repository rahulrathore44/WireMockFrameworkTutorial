package org.learning.communication;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern;
import com.github.tomakehurst.wiremock.matching.MatchesJsonSchemaPattern;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
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

    @Test
    @DisplayName("Verify 201 status code with JSON Path Matcher")
    public void test201CreatedWithJsonPathMatcher() throws Exception {

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
        //var jsonPathMatcher = new MatchesJsonPathPattern("$.name");
        var jsonPathMatcher = new MatchesJsonPathPattern("$.category.name");
        //var jsonPathWithValue = new MatchesJsonPathPattern("$.category.name", WireMock.equalTo("Dog"));
        var jsonPathWithValue = new MatchesJsonPathPattern("$.tags[0]", WireMock.equalToJson("""
                {
                			"id": 1,
                			"name": "Four legs"
                		}
                """, true, true));
        var stubForPost = WireMock.post("/pet")
                .withHeader(HttpHeaders.ACCEPT, equalTo)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.including(WireMock.equalToIgnoreCase(configuration.getContentType().getMimeType()), endsWith))
                //.withRequestBody(WireMock.equalToJson(requestBodyInJson, true, true))
                //.withRequestBody(jsonPathMatcher)
                .withRequestBody(jsonPathWithValue)
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
    @DisplayName("Verify 201 status code using Json Unit Placeholder for matching")
    public void test201CreatedWithJsonUnitPlaceHolder() throws Exception {
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
                			"id": 156,
                			"name": "Four legs dog"
                		}
                	],
                	"status": "sold"
                }
                """.stripIndent().trim();

        var equalTo = new EqualToPattern(configuration.getContentType().getMimeType(), true);
        var endsWith = new EndsWithPattern(configuration.getContentType().getMimeType(), "json");
        var jsonPathWithValue = new MatchesJsonPathPattern("$.tags[0]", WireMock.equalToJson("""
                {
                			"id": "${json-unit.any-number}",
                			"name": "${json-unit.regex}^[A-Za-z ]+$"
                		}
                """, true, true));
        var stubForPost = WireMock.post("/pet")
                .withHeader(HttpHeaders.ACCEPT, equalTo)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.including(WireMock.equalToIgnoreCase(configuration.getContentType().getMimeType()), endsWith))
                .withRequestBody(jsonPathWithValue)
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
    @DisplayName("Verify the 201 status code using request body matcher that uses JSON schema")
    public void test201CreateWithJsonSchema() throws Exception {
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
                			"id": 156,
                			"name": "Four legs dog"
                		}
                	],
                	"status": "sold"
                }
                """.stripIndent().trim();

        var equalTo = new EqualToPattern(configuration.getContentType().getMimeType(), true);
        var endsWith = new EndsWithPattern(configuration.getContentType().getMimeType(), "json");
        var jsonPathWithValue = new MatchesJsonPathPattern("$.tags[0]", WireMock.equalToJson("""
                {
                			"id": "${json-unit.any-number}",
                			"name": "${json-unit.regex}^[A-Za-z ]+$"
                		}
                """, true, true));

        var jsonSchema = """
                {
                  "$schema": "http://json-schema.org/draft-04/schema#",
                  "type": "object",
                  "properties": {
                    "id": {
                      "type": "integer"
                    },
                    "name": {
                      "type": "string"
                    },
                    "category": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "integer"
                        },
                        "name": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "id",
                        "name"
                      ]
                    },
                    "photoUrls": {
                      "type": "array",
                      "items": [
                        {
                          "type": "string"
                        }
                      ]
                    },
                    "tags": {
                      "type": "array",
                      "items": [
                        {
                          "type": "object",
                          "properties": {
                            "id": {
                              "type": "integer"
                            },
                            "name": {
                              "type": "string"
                            }
                          },
                          "required": [
                            "id",
                            "name"
                          ]
                        }
                      ]
                    },
                    "status": {
                      "type": "string"
                    }
                  },
                  "required": [
                    "id",
                    "name",
                    "category",
                    "photoUrls",
                    "tags",
                    "status"
                  ]
                }
                """.stripIndent().trim();
        var jsonSchemaMatcher = new MatchesJsonSchemaPattern(jsonSchema);
        var stubForPost = WireMock.post("/pet")
                .withHeader(HttpHeaders.ACCEPT, equalTo)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.including(WireMock.equalToIgnoreCase(configuration.getContentType().getMimeType()), endsWith))
                //.withRequestBody(jsonSchemaMatcher)
                .withRequestBody(WireMock.matchingJsonSchema(jsonSchema))
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
    @DisplayName("Verify 201 status code using Url Pattern for matching")
    public void test201CreatedWithUrlPattern() throws Exception {
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
                			"id": 156,
                			"name": "Four legs dog"
                		}
                	],
                	"status": "sold"
                }
                """.stripIndent().trim();

        var equalTo = new EqualToPattern(configuration.getContentType().getMimeType(), true);
        var endsWith = new EndsWithPattern(configuration.getContentType().getMimeType(), "json");
        var jsonPathWithValue = new MatchesJsonPathPattern("$.tags[0]", WireMock.equalToJson("""
                {
                			"id": "${json-unit.any-number}",
                			"name": "${json-unit.regex}^[A-Za-z ]+$"
                		}
                """, true, true));


        //var wordMatcher = WireMock.not(WireMock.containing("pet"));
        var wordMatcher = WireMock.containing("pet");
        var pathMatcher = new UrlPathPattern(wordMatcher, false);
        var stubForPost = WireMock.post(pathMatcher)
                .withHeader(HttpHeaders.ACCEPT, equalTo)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.including(WireMock.equalToIgnoreCase(configuration.getContentType().getMimeType()), endsWith))
                .withRequestBody(jsonPathWithValue)
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
    @DisplayName("verify the 400 status code for post when request body has invalid data")
    public void test400StatusCode() throws Exception {

        var requestBodyInJson = """
                {
                	"id": -96,
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
                			"id": 156,
                			"name": "Four legs dog"
                		}
                	],
                	"status": "sold"
                }
                """.stripIndent().trim();

        var errorResponse = """
                {
                  "error": "Invalid pet ID: -1. Pet ID must be a positive integer."
                }
                """.stripIndent().trim();

        var equalTo = new EqualToPattern(configuration.getContentType().getMimeType(), true);
        var endsWith = new EndsWithPattern(configuration.getContentType().getMimeType(), "json");
        var jsonPathWithValue = new MatchesJsonPathPattern("$.tags[0]", WireMock.equalToJson("""
                {
                			"id": "${json-unit.any-number}",
                			"name": "${json-unit.regex}^[A-Za-z ]+$"
                		}
                """, true, true));


        //var wordMatcher = WireMock.not(WireMock.containing("pet"));
        var wordMatcher = WireMock.containing("pet");
        var pathMatcher = new UrlPathPattern(wordMatcher, false);
        var requestBodyMatcher = new MatchesJsonPathPattern("$.id", WireMock.matching("^-(?!0)\\d+$"));
        var stubForPost = WireMock.post(pathMatcher)
                .withHeader(HttpHeaders.ACCEPT, equalTo)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.including(WireMock.equalToIgnoreCase(configuration.getContentType().getMimeType()), endsWith))
                .withRequestBody(requestBodyMatcher)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                        .withResponseBody(new Body(errorResponse))
                );
        server.stubFor(stubForPost);
        try {

            var response = communication.create(requestBodyInJson);

            // Validation for response not null
            Assertions.assertNotNull(response);

            // Validation on response status code
            var responseData = response.returnResponse();
            Assertions.assertEquals(HttpStatus.SC_BAD_REQUEST, responseData.getStatusLine().getStatusCode());

            // Validation on response headers
            var header = Arrays.stream(responseData.getHeaders(HttpHeaders.CONTENT_TYPE)).findFirst();
            if (header.isPresent()) {
                Assertions.assertEquals(configuration.getContentType().getMimeType(), header.get().getValue());
            } else {
                Assertions.fail("Content Type header is not present in the response");
            }

            // Validation on response body
            var actualResponseBody = EntityUtils.toString(responseData.getEntity());
            Assertions.assertEquals(errorResponse, actualResponseBody);

        } finally {
            server.removeStub(stubForPost);

        }

    }

}
