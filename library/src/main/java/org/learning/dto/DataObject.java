package org.learning.dto;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.StringEntity;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DataObject {

    private final int statusCode;

    private final Map<String, String> responseHeaders;

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public HttpEntity getResponseBody() {
        return responseBody;
    }

    private final HttpEntity responseBody;

    private DataObject(int statusCode, Map<String, String> responseHeaders, HttpEntity responseBody) {
        this.statusCode = statusCode;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static DataObject fromResponse(Response response) {
        try {
            var responseData = response.returnResponse();
            var status = responseData.getStatusLine().getStatusCode();
            var headers = Arrays.stream(responseData.getAllHeaders()).collect(Collectors.toMap(Header::getName, Header::getValue));
            var body = responseData.getEntity();
            return new DataObject(status, headers, body);

        } catch (Exception ex) {
            return new DataObject(200, new HashMap<>(), new StringEntity("", Charset.defaultCharset()));
        }
    }

}
