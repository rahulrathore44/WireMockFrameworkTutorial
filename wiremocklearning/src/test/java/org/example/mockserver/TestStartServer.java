package org.example.mockserver;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestStartServer {

    @Test
    public void testServerStart() throws IOException {
        var mockServer = new WireMockServer();
        mockServer.start();
        var response = Request.Get("http://localhost:8080/__admin/mappings").execute().returnResponse();
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        mockServer.stop();
    }

    @Test
    public void testServerStartOnConfigurePort() throws IOException {
        int port = 9001;
        var mockServer = new WireMockServer(port);
        mockServer.start();
        var response = Request.Get("http://localhost:" + port + "/__admin/mappings").execute().returnResponse();
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        mockServer.stop();
    }

    @Test
    public void testServerStartOnDynamicPort() throws IOException {
        var config = new WireMockConfiguration();
        var mockServer = new WireMockServer(config.dynamicPort());
        mockServer.start();
        int port = mockServer.port();
        System.out.println("Port Number: " + port);
        var response = Request.Get("http://localhost:" + port + "/__admin/mappings").execute().returnResponse();
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        mockServer.stop();
    }
}
