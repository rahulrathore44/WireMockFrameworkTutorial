package org.learning.transform;

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
import org.learning.dto.DataObject;
import org.learning.models.CategoryBuilder;
import org.learning.models.PetBuilder;
import org.learning.models.TagBuilder;
import org.learning.validation.JsonValidator;
import org.learning.validation.Validator;
import org.learning.validation.XMLValidator;

import java.util.List;

public class TestTransformDataForXmlDataType {

    private static Communication communication;
    private static Configuration configuration;
    private static Validator validator;
    private static TransformData transformData;
    private static WireMockServer wireMockServer;
    private static WireMockConfiguration wireMockConfiguration;

    @BeforeAll
    public static void stepUp() {
        wireMockConfiguration = new WireMockConfiguration();
        wireMockServer = new WireMockServer(wireMockConfiguration.dynamicPort());
        wireMockServer.start();
        configuration = new Configuration.ConfigurationBuilder().withUrl("http://localhost:" + wireMockServer.port())
                .withContentType(ContentType.APPLICATION_XML)
                .build();
        communication = new CommunicationImpl(configuration);
        transformData = new TransformDataImplXml();
    }

    @AfterAll
    public static void tearDown() {
        if (wireMockServer.isRunning())
            wireMockServer.shutdownServer();

        wireMockConfiguration = null;
        communication = null;
        configuration = null;
        transformData = null;
    }

    @Test
    @DisplayName("Verify the data transform layer for xml data type in list")
    public void testTransformDateForXmlList() throws Exception {
        var tag = TagBuilder.create().withId(1).withName("Happy Dog").build();
        var category = CategoryBuilder.create().withId(2).withName("Dog").build();
        var petOne = PetBuilder.create().withId(1).withName("Bruno").withStatus("sold")
                .withCategory(category)
                .withTags(List.of(tag))
                .withPhotoUrls(List.of("http://locahost:9090/dog.jpg"))
                .build();

        var petTwo = PetBuilder.create().withId(2).withName("Bruno").withStatus("sold")
                .withCategory(category)
                .withTags(List.of(tag))
                .withPhotoUrls(List.of("http://locahost:9090/dog.jpg"))
                .build();

        var listOfPets = List.of(petOne, petTwo);

        var responseBody = transformData.deSerialize(listOfPets);

        var body = new Body(responseBody);
        var stub = WireMock.get("/pet/all")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(HttpHeaders.CONTENT_TYPE, configuration.getContentType().getMimeType())
                        .withResponseBody(body));

        wireMockServer.stubFor(stub);
        try {

            var response = communication.getAll();

            // Validation on response
            Assertions.assertNotNull(response);

            var dto = DataObject.fromResponse(response);
            validator = new XMLValidator(dto);
            validator.validateStatusCode();
            validator.validateResponseHeaders();
            var responseObject = EntityUtils.toString(dto.getResponseBody());
            var responseObjects = transformData.serializes(responseObject);
            Assertions.assertEquals(2, responseObjects.size());
        } finally {
            wireMockServer.removeStub(stub);
        }

    }

}
