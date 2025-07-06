package org.learning.communication;

import org.apache.http.client.fluent.Response;
import org.learning.config.Configuration;

public class CommunicationImpl implements Communication {

    private final Configuration config;

    public CommunicationImpl(Configuration config) {
        this.config = config;
    }

    @Override
    public Response create(String data) {
        return null;
    }

    @Override
    public Response getAll() {
        return null;
    }
}
