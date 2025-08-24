package org.learning.communication.assignment;

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
import org.learning.exception.UnHandledException;
import org.learning.validation.Validator;
import org.learning.validation.XMLValidator;

public class Assignment_5_Stub_For_XMLValidator_500 {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer wireMockServer;
    private static Communication communication;
    private static Configuration configuration;
    private Validator validator;

    @BeforeAll
    public static void setUp() {
        wireMockConfiguration = new WireMockConfiguration();
        wireMockServer = new WireMockServer(wireMockConfiguration.dynamicPort());
        wireMockServer.start();
        configuration = new Configuration.ConfigurationBuilder().withUrl("http://localhost:" + wireMockServer.port()).withContentType(ContentType.APPLICATION_XML).build();
        communication = new CommunicationImpl(configuration);
    }

    @AfterAll
    public static void tearDown() {
        if (wireMockServer.isRunning())
            wireMockServer.shutdownServer();

        wireMockServer = null;
        communication = null;
        configuration = null;
    }

    @Test
    @DisplayName("Verify the exception when server return 500 status code")
    public void test500StatusCode() throws Exception {
        var requestBodyInXML = """
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
                .withHeader(HttpHeaders.ACCEPT, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .withRequestBody(WireMock.matchingXPath("/pet/id/text()", WireMock.equalTo("-1")))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                                .withResponseBody(new Body(responseBody))
                );


        try {
            wireMockServer.stubFor(stub);
            var response = communication.create(requestBodyInXML);
            var dto = DataObject.fromResponse(response);
            validator = new XMLValidator(dto);
            Assertions.assertThrows(UnHandledException.class, () -> {
                validator.validateStatusCode();
            });
            validator.validateResponseHeaders();
        } finally {
            wireMockServer.removeStub(stub);
        }

    }

}
