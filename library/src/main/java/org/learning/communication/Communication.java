package org.learning.communication;

import org.apache.http.client.fluent.Response;

import java.io.File;

public interface Communication {

    Response create(String data) throws Exception;

    Response getAll() throws Exception;

    Response uploadDataUsingFile(File file, String format) throws Exception;
}
