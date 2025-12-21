package org.learning.extensions;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.*;
import org.learning.communication.Communication;
import org.learning.communication.CommunicationImpl;
import org.learning.config.Configuration;

@WireMockTest
public class TestDeleteWithJunitExt {

    private static Communication communication;
    private static Configuration configuration;

    @BeforeAll
    public static void setUp(WireMockRuntimeInfo wireMockRuntimeInfo) {
        configuration = new Configuration.ConfigurationBuilder()
                .withContentType(ContentType.APPLICATION_JSON)
                .withUrl(wireMockRuntimeInfo.getHttpBaseUrl())
                .build();
        communication = new CommunicationImpl(configuration);
    }

    @Test
    @DisplayName("Verify the delete request using JUnit Wiremock ext")
    public void testDeleteReqWithBasicAuth(WireMockRuntimeInfo wireMockRuntimeInfo) throws Exception {

        var stub = WireMock.delete(WireMock.urlPathTemplate("/pet/{petId}"))
                .withPathParam("petId", WireMock.equalTo("1"))
                .withBasicAuth("admin", "welcome")
                .willReturn(
                        WireMock.ok()
                );

        var wiremock = wireMockRuntimeInfo.getWireMock();
        wiremock.register(stub);
        var response = communication.deletePetById("1", "admin", "welcome");
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.SC_OK, response.returnResponse().getStatusLine().getStatusCode());
    }

    @AfterAll
    public static void tearDown() {
        communication = null;
        configuration = null;
    }
}
