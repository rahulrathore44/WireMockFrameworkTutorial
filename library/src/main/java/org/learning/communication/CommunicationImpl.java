package org.learning.communication;

import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.learning.config.Configuration;

import java.io.File;

public class CommunicationImpl implements Communication {

    private final Configuration config;

    public CommunicationImpl(Configuration config) {
        this.config = config;
    }

    @Override
    public Response create(String data) throws Exception {
        var response = Request.Post(config.getUrl() + "/pet").setHeaders(
                new BasicHeader(HttpHeaders.CONTENT_TYPE, config.getContentType().getMimeType()),
                new BasicHeader(HttpHeaders.ACCEPT, config.getContentType().getMimeType())
        ).body(new StringEntity(data)).execute();
        return response;
    }

    @Override
    public Response getAll() throws Exception {
        var response = Request.Get(config.getUrl() + "/pet/all").setHeaders(new BasicHeader(HttpHeaders.ACCEPT, config.getContentType().getMimeType())).execute();
        return response;
    }

    @Override
    public Response uploadDataUsingFile(File file, String format) throws Exception {
        return null;
    }
}
