package org.learning.communication;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.learning.config.Configuration;

import java.util.Arrays;

public class TestTemplate {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer server;
    private static Configuration configuration;
    private static Communication communication;

    @BeforeAll
    public static void setUp() {
        wireMockConfiguration = new WireMockConfiguration().dynamicPort().globalTemplating(true);
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
    @DisplayName("Verify that 201 status code is returned from server for post call via response template")
    public void test201StatusCodeWithResponseTemplate() throws Exception {
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

        var templateBody = "{{jsonPath request.body '$.category'}}";
        var contentTypeHeader = "{{request.headers.Accept}}";
        var stubForPost = WireMock.post("/pet")
                .withRequestBody(WireMock.equalToJson(requestBodyInJson, true, true))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_CREATED)
                        .withHeader(HttpHeaders.CONTENT_TYPE, contentTypeHeader)
                        .withResponseBody(new Body(templateBody))
                );
        server.stubFor(stubForPost);
        try {

            var response = communication.create(requestBodyInJson);

            // Validation for response not null
            Assertions.assertNotNull(response);
            var responseObj = response.returnResponse();
            var headers = responseObj.getAllHeaders();
            var body = EntityUtils.toString(responseObj.getEntity());
            System.out.println("Response Body : " + body);
            System.out.println(Arrays.toString(headers));


        } finally {
            server.removeStub(stubForPost);

        }

    }

}
