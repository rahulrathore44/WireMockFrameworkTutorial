package org.learning.communication.assignment;

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

public class Assignment_2_Stub_For_XML {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer server;
    private static Configuration configuration;
    private static Communication communication;

    @BeforeAll
    public static void setUp() {
        wireMockConfiguration = new WireMockConfiguration().dynamicPort();
        server = new WireMockServer(wireMockConfiguration);
        server.start();
        configuration = new Configuration.ConfigurationBuilder().withUrl("http://localhost:" + server.port()).withContentType(ContentType.APPLICATION_XML).build();
        communication = new CommunicationImpl(configuration);
    }

    @Test
    @DisplayName("Verify that the server returns 200 ok status code with repose data")
    public void test200OkStatusCode() throws Exception {

        var responseBody = """
                    <pets>
                         <pet>
                             <id>1</id>
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
                     </pets>
                """.trim();

        var body = new Body(responseBody);
        var stub = WireMock.get("/pet/all").willReturn(WireMock
                .aResponse().withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                .withHeader(HttpHeaders.CONTENT_LANGUAGE, "en-US")
                .withResponseBody(body));
        server.stubFor(stub);
        try {

            var response = communication.getAll();

            // Validation on response
            Assertions.assertNotNull(response);

            var responseData = response.returnResponse();

            // Validation on status code
            Assertions.assertEquals(HttpStatus.SC_OK, responseData.getStatusLine().getStatusCode());

            // Validation on response header
            var headerContentType = Arrays.stream(responseData.getHeaders(HttpHeaders.CONTENT_TYPE)).findFirst();
            var headerContentLang = Arrays.stream(responseData.getHeaders(HttpHeaders.CONTENT_LANGUAGE)).findFirst();

            if (headerContentType.isPresent() && headerContentLang.isPresent()) {
                // Validation for response header
                Assertions.assertEquals(headerContentType.get().getValue(), configuration.getContentType().getMimeType());
                Assertions.assertEquals(headerContentLang.get().getValue(), "en-US");
            } else {
                Assertions.fail("Response dose not have the required header.");
            }

            // validation on response body
            Assertions.assertNotNull(responseData.getEntity());

            Assertions.assertEquals(responseBody, EntityUtils.toString(responseData.getEntity()));
        } finally {
            server.removeStub(stub);
        }

    }

    @AfterAll
    public static void tearDown() {
        if (server.isRunning())
            server.shutdownServer();

        configuration = null;
        communication = null;
    }
}
