package org.learning.communication.assignment;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Body;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.learning.communication.Communication;
import org.learning.communication.CommunicationImpl;
import org.learning.config.Configuration;

public class Assignment_4_Stub_For_UrlPattern {

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
    public void testStubWithRegularExpression() throws Exception {
        var bodyInXML = """
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

        var stubForPost = WireMock.post(WireMock.urlPathMatching("^/pet(?:/all)?$"))
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .withHeader(HttpHeaders.ACCEPT, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .withRequestBody(WireMock.equalToXml(bodyInXML))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.SC_CREATED)
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                );

        var stubForGet = WireMock.get(WireMock.urlPathMatching("^/pet(?:/all)?$"))
                .withHeader(HttpHeaders.ACCEPT, WireMock.equalTo(configuration.getContentType().getMimeType()))
                .willReturn(
                        WireMock.aResponse()
                                .withStatus(HttpStatus.SC_OK)
                                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                                .withResponseBody(new Body(bodyInXML))
                );


        try {
            wireMockServer.stubFor(stubForPost);
            wireMockServer.stubFor(stubForGet);
            // POST
            var response = communication.create(bodyInXML);
            var responseData = response.returnResponse();
            Assertions.assertEquals(HttpStatus.SC_CREATED, responseData.getStatusLine().getStatusCode());

            //GET
            response = communication.getAll();
            responseData = response.returnResponse();
            Assertions.assertEquals(HttpStatus.SC_OK, responseData.getStatusLine().getStatusCode());
        } finally {
            wireMockServer.removeStub(stubForPost);
            wireMockServer.removeStub(stubForGet);
        }

    }

}
