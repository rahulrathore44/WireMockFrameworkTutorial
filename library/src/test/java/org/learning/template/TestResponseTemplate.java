package org.learning.template;

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

import java.util.Arrays;

public class TestResponseTemplate {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer wireMockServer;
    private static Communication communication;
    private static Configuration configuration;

    @BeforeAll
    public static void setUp() {
        wireMockConfiguration = new WireMockConfiguration().templatingEnabled(true);
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
        communication = null;
        configuration = null;
    }

    @Test
    @DisplayName("Verify the response template using header and body")
    public void testResponseTemplateWithHeaderAndBody() throws Exception {
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

        // {{request.headers.<header-name>}}
        // {{request.headers.Accept}}
        var acceptHeaderValue = "{{request.headers.Accept}}";
        // {{request.body}}
        // {{request.method}} : To get the request HTTP method
        // {{request.url}} : To get the request URL
        // {{request.path.<path-param-name>}} : To ge the path parameters
        var responseBody = "{{request.body}}";
        var stubForPost = WireMock.post("/pet")
                .withRequestBody(WireMock.equalToJson(requestBodyInJson, true, true))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_CREATED)
                        .withHeader(HttpHeaders.CONTENT_TYPE, acceptHeaderValue)
                        .withResponseBody(new Body(responseBody))
                        .withTransformers("response-template")
                );
        wireMockServer.stubFor(stubForPost);
        try {

            var response = communication.create(requestBodyInJson);
            Assertions.assertNotNull(response);
            var responseObj = response.returnResponse();
            var headers = responseObj.getAllHeaders();
            var body = EntityUtils.toString(responseObj.getEntity());
            System.out.println("Response Body: " + body);
            System.out.println("Response Headers: " + Arrays.toString(headers));
        } finally {
            wireMockServer.removeStub(stubForPost);

        }
    }

    @Test
    @DisplayName("Verify the response template using JSON Path helpers")
    public void testResponseTemplateWithJsonPathHelpers() throws Exception {
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

        // {{jsonPath request.body 'json-path'}}
        // {{parseJson request.body 'bodyVar'}}
        // {{toJson request.headers}}
        // {jsonMerge object1 object2}}



        var acceptHeaderValue = "{{toJson request.headers}}";
        //var responseBody = "{{jsonPath request.body '$.category.name'}}";
        //var responseBody = "{{jsonPath request.body '$.category'}}";
        /* var responseBody = "{{parseJson request.body 'bodyJson'}}\n" +
                "{{bodyJson.photoUrls}}"; */

        var responseBody = "{{parseJson request.body 'bodyJson'}}\n" +
                "{{bodyJson.category.id}}";
        var stubForPost = WireMock.post("/pet")
                .withRequestBody(WireMock.equalToJson(requestBodyInJson, true, true))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_CREATED)
                        .withHeader(HttpHeaders.CONTENT_TYPE, acceptHeaderValue)
                        .withResponseBody(new Body(responseBody))
                        .withTransformers("response-template")
                );
        wireMockServer.stubFor(stubForPost);
        try {

            var response = communication.create(requestBodyInJson);
            Assertions.assertNotNull(response);
            var responseObj = response.returnResponse();
            var headers = responseObj.getAllHeaders();
            var body = EntityUtils.toString(responseObj.getEntity());
            System.out.println("Response Body: " + body);
            System.out.println("Response Headers: " + Arrays.toString(headers));
        } finally {
            wireMockServer.removeStub(stubForPost);

        }
    }

    @Test
    @DisplayName("Verify the response template using handle bar utility helpers")
    public void testResponseTemplateWithHandleBarUtilityHelpers() throws Exception {
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

        var acceptHeaderValue = "{{toJson request.headers}}";
        //var responseBody = "{{randomInt}}";
        //var responseBody = "{{base64 request.headers.Accept}}";
        //var responseBody = "{{base64 request.headers.Accept decode=true}}";
        //var responseBody = "{{now}}";
        //var responseBody = "{{now timezone='Australia/Sydney' format='yyyy-MM-dd HH:mm:ssZ'}}";
        //var responseBody = "{{lower request.body}}";
        var responseBody = "{{upper request.body}}";
        var stubForPost = WireMock.post("/pet")
                .withRequestBody(WireMock.equalToJson(requestBodyInJson, true, true))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_CREATED)
                        .withHeader(HttpHeaders.CONTENT_TYPE, acceptHeaderValue)
                        .withResponseBody(new Body(responseBody))
                        .withTransformers("response-template")
                );
        wireMockServer.stubFor(stubForPost);
        try {

            var response = communication.create(requestBodyInJson);
            Assertions.assertNotNull(response);
            var responseObj = response.returnResponse();
            var headers = responseObj.getAllHeaders();
            var body = EntityUtils.toString(responseObj.getEntity());
            System.out.println("Response Body: " + body);
            System.out.println("Response Headers: " + Arrays.toString(headers));
        } finally {
            wireMockServer.removeStub(stubForPost);

        }
    }
}
