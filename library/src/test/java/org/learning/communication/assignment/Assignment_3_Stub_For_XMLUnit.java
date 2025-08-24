package org.learning.communication.assignment;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.*;
import org.learning.communication.Communication;
import org.learning.communication.CommunicationImpl;
import org.learning.config.Configuration;

public class Assignment_3_Stub_For_XMLUnit {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer wireMockServer;
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

    @AfterAll
    public static void tearDown() {
        if (wireMockServer.isRunning())
            wireMockServer.shutdownServer();

        wireMockServer = null;
        communication = null;
        configuration = null;
    }

    @Test
    @DisplayName("Verify that the server returns 200 ok status code with repose data")
    public void test200OkStatusCodeWithXMLUnitPlaceHolders() throws Exception {
        var requestBodyInXML = """
                <pet>
                    <id>11</id>
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

        var bodyWithPlaceHolders = """
                <pet>
                    <id>${xmlunit.isNumber}</id>
                    <name>Bruno</name>
                    <category>
                        <id>1</id>
                        <name>Dog</name>
                    </category>
                    <status>${xmlunit.ignore}</status>
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

        var stub = WireMock.post("/pet")
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .withHeader(HttpHeaders.ACCEPT, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .withRequestBody(WireMock.equalToXml(bodyWithPlaceHolders, true))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.SC_CREATED)
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                );


        try {
            wireMockServer.stubFor(stub);
            var response = communication.create(requestBodyInXML);
            var responseData = response.returnResponse();
            Assertions.assertEquals(HttpStatus.SC_CREATED, responseData.getStatusLine().getStatusCode());
        } finally {
            wireMockServer.removeStub(stub);
        }

    }

}
