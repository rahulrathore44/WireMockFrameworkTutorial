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

public class TestResponseTemplateXML {

    private static WireMockConfiguration wireMockConfiguration;
    private static WireMockServer wireMockServer;
    private static Communication communication;
    private static Configuration configuration;

    @BeforeAll
    public static void setUp() {
        wireMockConfiguration = new WireMockConfiguration().templatingEnabled(true);
        wireMockServer = new WireMockServer(wireMockConfiguration.dynamicPort());
        wireMockServer.start();
        configuration = new Configuration.ConfigurationBuilder().withUrl("http://localhost:" + wireMockServer.port()).withContentType(ContentType.APPLICATION_XML).build();
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
    @DisplayName("Verify the response template using XPath helpers")
    public void testResponseTemplateWithXPathHelpers() throws Exception {
        var requestBodyInXml = """
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
                """.trim();

        // {{xPath request.body '<xPath to the node>'}}
        // {{formatXml object1}}
        var acceptHeaderValue = "{{toJson request.headers}}";
        //var responseBody = "{{xPath request.body '/pet/category/name/text()'}}";
        var responseBody = "{{xPath request.body '/pet/category'}}";
        var stubForPost = WireMock.post("/pet")
                .withRequestBody(WireMock.equalToXml(requestBodyInXml))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_CREATED)
                        .withHeader(HttpHeaders.CONTENT_TYPE, acceptHeaderValue)
                        .withResponseBody(new Body(responseBody))
                        .withTransformers("response-template")
                );
        wireMockServer.stubFor(stubForPost);
        try {

            var response = communication.create(requestBodyInXml);
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
