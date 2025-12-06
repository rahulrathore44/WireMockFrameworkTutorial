package org.learning.communication;

import org.apache.http.client.fluent.Response;

import java.io.File;

public interface Communication {

    Response create(String data) throws Exception;

    Response getAll() throws Exception;

    Response uploadDataUsingFile(File file, String format) throws Exception;

    Response findPetsByStatus(String status) throws Exception;

    Response findPetById(String petId) throws Exception;

    Response updatePetById(String petId, String data) throws Exception;

    Response deletePetById(String petId, String user, String pass) throws Exception;
}
