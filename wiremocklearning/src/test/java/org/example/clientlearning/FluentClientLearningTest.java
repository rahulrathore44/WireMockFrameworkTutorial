package org.example.clientlearning;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FluentClientLearningTest {


    @Test
    @DisplayName("Send the Get request to /breeds endpoint")
    public void testGetRequest() throws IOException {
        var response = Request.Get("https://dogapi.dog/api/v2/breeds")
                .addHeader(HttpHeaders.ACCEPT, "application/json")
                .execute()
                .returnResponse();
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        System.out.println(new String(response.getEntity().getContent().readAllBytes()));

    }
}
