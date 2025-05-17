package org.example.mockserver;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.jupiter.api.*;

import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestStartServerCombine {

    private WireMockServer server;
    private WireMockConfiguration config;

    @BeforeAll
    public void setUp() {
        config = new WireMockConfiguration();
        server = new WireMockServer(config.dynamicPort());
        server.start();
    }

    @AfterAll
    public void tearDown() {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }

    @Test
    public void testServerStart() throws IOException {
        int port = server.port();
        var response = Request.Get("http://localhost:" + port + "/__admin/mappings").execute().returnResponse();
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testServerStartOnConfigurePort() throws IOException {
        int port = server.port();
        var response = Request.Get("http://localhost:" + port + "/__admin/mappings").execute().returnResponse();
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testServerStartOnDynamicPort() throws IOException {
        int port = server.port();
        System.out.println("Port Number: " + port);
        var response = Request.Get("http://localhost:" + port + "/__admin/mappings").execute().returnResponse();
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }
}
