package org.learning.communication;

import org.apache.http.client.fluent.Response;

public interface Communication {

    Response create(String data);

    Response getAll();
}
