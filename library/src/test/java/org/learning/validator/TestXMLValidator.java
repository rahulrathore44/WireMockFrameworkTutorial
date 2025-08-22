package org.learning.validator;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.*;
import org.learning.communication.Communication;
import org.learning.communication.CommunicationImpl;
import org.learning.config.Configuration;
import org.learning.dto.DataObject;
import org.learning.exception.InvalidPayloadException;
import org.learning.validation.Validator;
import org.learning.validation.XMLValidator;

public class TestXMLValidator {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer wireMockServer;
    private static Validator validator;
    private static Communication communication;
    private static Configuration configuration;

    @BeforeAll
    public static void setUp() {
        wireMockConfiguration = new WireMockConfiguration();
        wireMockServer = new WireMockServer(wireMockConfiguration.dynamicPort());
        wireMockServer.start();
        configuration = new Configuration.ConfigurationBuilder().withUrl("http://localhost:" + wireMockServer.port()).withContentType(ContentType.APPLICATION_XML).build();
        communication = new CommunicationImpl(configuration);

    }

    @Test
    @DisplayName("Verify the validator when server returns 400 status code")
    public void test400StatusCode() throws Exception {
        var requestBodyInXml = """
                <pet>
                    <id>-1</id>
                    <name>Bruno</name>
                    <category>
                        <id>1</id>
                        <name>Dog</name>
                    </category>
                    <status>sold</status>
                    <photoUrls>
                        <photoUrls>http://localhost:8080/pic.jpg</photoUrls>
                    </photoUrls>
                    <tags>
                        <tags>
                            <id>1</id>
                            <name>Good Dog</name>
                        </tags>
                    </tags>
                </pet>
                """.stripIndent().trim();

        var responseBody = """
                <ErrorResponse>
                    <error>Invalid pet ID: -1. Pet ID must be a positive integer.</error>
                </ErrorResponse>
                """.stripIndent().trim();

        var stub = WireMock.post("/pet")
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .withRequestBody(WireMock.matchingXPath("/pet/id/text()", WireMock.equalTo("-1")))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.SC_BAD_REQUEST)
                                .withResponseBody(new Body(responseBody))
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                );

        wireMockServer.stubFor(stub);

        try {

            var response = communication.create(requestBodyInXml);
            var dto = DataObject.fromResponse(response);
            validator = new XMLValidator(dto);

            Assertions.assertThrows(InvalidPayloadException.class, () -> {
                validator.validateStatusCode();
            });
        } finally {
            wireMockServer.removeStub(stub);
        }

    }

    @AfterAll
    public static void tearDown() {
        if (wireMockServer.isRunning())
            wireMockServer.shutdown();

        wireMockConfiguration = null;
        wireMockServer = null;
        communication = null;
        configuration = null;
    }
}
