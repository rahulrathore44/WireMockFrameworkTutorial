package org.learning.communication;

import org.apache.http.client.fluent.Response;
import org.learning.retry.RetryImpl;

import java.io.File;

public class CommunicationImplWithRetry implements Communication {

    private final Communication delegate;
    private final RetryImpl retry;

    public CommunicationImplWithRetry(Communication delegate) {
        this.delegate = delegate;
        this.retry = new RetryImpl();
    }

    @Override
    public Response create(String data) throws Exception {
        return retry.executeWithRetry(() -> {
            try {
                return delegate.create(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Response getAll() throws Exception {
        return null;
    }

    @Override
    public Response uploadDataUsingFile(File file, String format) throws Exception {
        return null;
    }

    @Override
    public Response findPetsByStatus(String status) throws Exception {
        return null;
    }

    @Override
    public Response findPetById(String petId) throws Exception {
        return null;
    }
}
