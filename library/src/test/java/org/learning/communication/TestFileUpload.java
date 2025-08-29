package org.learning.communication;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.matching.MultipartValuePattern;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.*;
import org.learning.config.Configuration;
import org.learning.utils.FileReaderUtils;

import java.io.File;
import java.util.Arrays;

public class TestFileUpload {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer wireMockServer;
    private static FileReaderUtils readerUtils;
    private static Communication communication;
    private static Configuration configuration;

    @BeforeAll
    public static void setUp() {
        wireMockConfiguration = new WireMockConfiguration();
        wireMockServer = new WireMockServer(wireMockConfiguration.dynamicPort());
        wireMockServer.start();
        readerUtils = new FileReaderUtils();
        configuration = new Configuration.ConfigurationBuilder().withUrl("http://localhost:" + wireMockServer.port()).withContentType(ContentType.APPLICATION_JSON).build();
        communication = new CommunicationImpl(configuration);
    }

    @Test
    @DisplayName("Verify the file upload using multipart/form-data type")
    public void testMultipartFormData() throws Exception {
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
        } finally {
            wireMockServer.removeStub(stub);
        }

    }

    @AfterAll
    public static void tearDown() {
        if (wireMockServer.isRunning())
            wireMockServer.shutdownServer();

        wireMockConfiguration = null;
        readerUtils = null;
        communication = null;
        configuration = null;


    }
}
