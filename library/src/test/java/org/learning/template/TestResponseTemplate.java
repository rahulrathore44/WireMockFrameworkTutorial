package org.learning.template;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.matching.MultipartValuePattern;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.learning.communication.Communication;
import org.learning.communication.CommunicationImpl;
import org.learning.config.Configuration;
import org.learning.utils.FileReaderUtils;

import java.io.File;
import java.util.Arrays;

public class TestResponseTemplate {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer wireMockServer;
    private static Communication communication;
    private static Configuration configuration;
    private static FileReaderUtils readerUtils;

    @BeforeAll
    public static void setUp() {
        wireMockConfiguration = new WireMockConfiguration().templatingEnabled(true).extensions(new CustomResponseTransformer());
        wireMockServer = new WireMockServer(wireMockConfiguration.dynamicPort());
        wireMockServer.start();
        configuration = new Configuration.ConfigurationBuilder().withUrl("http://localhost:" + wireMockServer.port()).withContentType(ContentType.APPLICATION_JSON).build();
        communication = new CommunicationImpl(configuration);
        readerUtils = new FileReaderUtils();
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

    @Test
    @DisplayName("Verify the file upload using multipart/form-data type using custom transformer")
    public void testMultipartFormDataWithCustomTransformer() throws Exception {
        var fileName = "Request_body_for_pet_in_json-list.txt";
        File file = readerUtils.readeFile(fileName);

        var stub = WireMock.post(WireMock.urlPathEqualTo("/pet/upload"))
                .withQueryParam("format", WireMock.equalTo("JSON"))
                .withMultipartRequestBody(
                        WireMock.aMultipart()
                                .withName("file")
                                .withFileName(fileName)
                                .withBody(WireMock.matchingJsonPath("$[0].id", WireMock.equalTo("3")))
                                .matchingType(MultipartValuePattern.MatchingType.ANY)
                ).willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.SC_OK)
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                                .withHeader("x-file-name", fileName)
                                .withTransformers("file-upload-transformer")
                );

        try {
            wireMockServer.stubFor(stub);
            var response = communication.uploadDataUsingFile(file, "JSON");
            Assertions.assertNotNull(response);
            // Validation on response status code
            var responseData = response.returnResponse();
            Assertions.assertEquals(HttpStatus.SC_OK, responseData.getStatusLine().getStatusCode());

            // Validation on response headers
            var header = Arrays.stream(responseData.getHeaders("x-file-name")).findFirst();
            if (header.isPresent()) {
                Assertions.assertEquals(fileName, header.get().getValue());
            } else {
                Assertions.fail("x-file-name header is not present in the response");
            }
            // validate the response content

            Assertions.assertEquals("[3, 4]", EntityUtils.toString(responseData.getEntity()));
        } finally {
            wireMockServer.removeStub(stub);
        }

    }
}
