package org.learning.communication;

import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicHeader;
import org.learning.config.Configuration;

public class CommunicationImpl implements Communication {

    private final Configuration config;

    public CommunicationImpl(Configuration config) {
        this.config = config;
    }

    @Override
    public Response create(String data) throws Exception {
        return null;
    }

    @Override
    public Response getAll() throws Exception {
        return Request.Get(this.config.getUrl() + "/pet/all").setHeaders(new BasicHeader(HttpHeaders.ACCEPT, this.config.getContentType().getMimeType())).execute();
    }
}
