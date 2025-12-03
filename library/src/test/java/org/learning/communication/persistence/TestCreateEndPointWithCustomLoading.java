package org.learning.communication.persistence;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.SingleRootFileSource;
import com.github.tomakehurst.wiremock.common.filemaker.FilenameMaker;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsSource;
import com.github.tomakehurst.wiremock.standalone.MappingsLoader;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.stubbing.StubMappings;
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
 * <p>
 * Ensure that the system has read and write access to the location configured in the withRootDirectory() method.
 */
public class TestCreateEndPointWithCustomLoading {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer server;
    private static Configuration configuration;
    private static Communication communication;

    @BeforeAll
    public static void setUp() {
        wireMockConfiguration = new WireMockConfiguration()
                .dynamicPort();
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
    @DisplayName("Verify the 201 status code along with request and response headers with custom loading")
    public void test201CreatedWithHeaderWithCustomLoading() throws Exception {
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

        try {
            server.loadMappingsUsing(new CustomMappingsLoader());
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
            server.resetMappings();

        }

    }

    @Test
    @DisplayName("Verify the 201 status code along with request and response headers with JsonFileMappingsSource")
    public void test201CreatedWithHeaderWithJsonFileMappingsSource() throws Exception {
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

        try {
            var source = new SingleRootFileSource("C:\\Data\\Folder1");
            var nameMaker = new FilenameMaker();
            var loader = new JsonFileMappingsSource(source, nameMaker);
            server.loadMappingsUsing(loader);
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
            server.resetMappings();

        }

    }


}

class CustomMappingsLoader implements MappingsLoader {

    private final String stub = """
            {
              "id" : "85420e18-9110-4cb3-8af1-787c4715669a",
              "name" : "StubForPost",
              "request" : {
                "url" : "/pet",
                "method" : "POST",
                "headers" : {
                  "Accept" : {
                    "equalTo" : "application/json",
                    "caseInsensitive" : true
                  },
                  "Content-Type" : {
                    "equalTo" : "application/json",
                    "caseInsensitive" : true
                  }
                },
                "bodyPatterns" : [ {
                  "equalToJson" : "{\\n\\t\\"id\\": 1,\\n\\t\\"name\\": \\"Bruno\\",\\n\\t\\"category\\": {\\n\\t\\t\\"id\\": 1,\\n\\t\\t\\"name\\": \\"Dog\\"\\n\\t},\\n\\t\\"photoUrls\\": [\\n\\t\\t\\"http://localhost:8909/pics/dog.jpg\\"\\n\\t],\\n\\t\\"tags\\": [\\n\\t\\t{\\n\\t\\t\\t\\"id\\": 1,\\n\\t\\t\\t\\"name\\": \\"Four legs\\"\\n\\t\\t}\\n\\t],\\n\\t\\"status\\": \\"sold\\"\\n}",
                  "ignoreArrayOrder" : true,
                  "ignoreExtraElements" : true
                } ]
              },
              "response" : {
                "status" : 201,
                "body" : "{\\n\\t\\"id\\": 1,\\n\\t\\"name\\": \\"Bruno\\",\\n\\t\\"category\\": {\\n\\t\\t\\"id\\": 1,\\n\\t\\t\\"name\\": \\"Dog\\"\\n\\t},\\n\\t\\"photoUrls\\": [\\n\\t\\t\\"http://localhost:8909/pics/dog.jpg\\"\\n\\t],\\n\\t\\"tags\\": [\\n\\t\\t{\\n\\t\\t\\t\\"id\\": 1,\\n\\t\\t\\t\\"name\\": \\"Four legs\\"\\n\\t\\t}\\n\\t],\\n\\t\\"status\\": \\"sold\\"\\n}",
                "headers" : {
                  "Content-Type" : "application/json"
                }
              },
              "uuid" : "85420e18-9110-4cb3-8af1-787c4715669a",
              "persistent" : true,
              "insertionIndex" : 2
            }
            """.trim().stripIndent();

    @Override
    public void loadMappingsInto(StubMappings stubMappings) {
        var mapping = StubMapping.buildFrom(stub);
        stubMappings.addMapping(mapping);
    }
}
