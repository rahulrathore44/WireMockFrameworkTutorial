package org.learning.communication;

import com.networknt.schema.utils.StringUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicHeader;
import org.learning.config.Configuration;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CommunicationImpl implements Communication {

    private final Configuration config;

    public CommunicationImpl(Configuration config) {
        this.config = config;
    }

    @Override
    public Response create(String data) throws Exception {
        var response = Request.Post(config.getUrl() + "/pet")
                .connectTimeout(config.getConnectionTimeout())
                .socketTimeout(config.getSocketTimeout())
                .setHeaders(
                        new BasicHeader(HttpHeaders.CONTENT_TYPE, config.getContentType().getMimeType()),
                        new BasicHeader(HttpHeaders.ACCEPT, config.getContentType().getMimeType())
                ).body(new StringEntity(data)).execute();
        return response;
    }

    @Override
    public Response getAll() throws Exception {
        var response = Request.Get(config.getUrl() + "/pet/all")
                .setHeaders(new BasicHeader(HttpHeaders.ACCEPT, config.getContentType().getMimeType()))
                .connectTimeout(config.getConnectionTimeout())
                .socketTimeout(config.getSocketTimeout())
                .execute();
        return response;
    }

    @Override
    public Response uploadDataUsingFile(File file, String format) throws Exception {
        var entity = MultipartEntityBuilder.create().addPart("file", new FileBody(file, config.getContentType(), file.getName())).build();
        var uri = new URIBuilder(config.getUrl() + "/pet/upload").addParameter("format", format).build();
        var response = Request.Post(uri)
                .connectTimeout(config.getConnectionTimeout())
                .socketTimeout(config.getSocketTimeout())
                .body(entity)
                .execute();
        return response;
    }

    @Override
    public Response findPetsByStatus(String status) throws Exception {
        var uri = new URIBuilder(config.getUrl() + "/pet/findPetsByStatus")
                .addParameter("status", status)
                .build();
        var response = Request.Get(uri)
                .connectTimeout(config.getConnectionTimeout())
                .socketTimeout(config.getSocketTimeout())
                .addHeader(HttpHeaders.ACCEPT, config.getContentType().getMimeType())
                .execute();
        return response;
    }

    @Override
    public Response findPetById(String petId) throws Exception {
        var uri = new URIBuilder(config.getUrl()).setPathSegments("pet", petId).build();
        var response = Request.Get(uri)
                .addHeader(HttpHeaders.ACCEPT, config.getContentType().getMimeType())
                .connectTimeout(config.getConnectionTimeout())
                .socketTimeout(config.getSocketTimeout())
                .execute();
        return response;
    }

    @Override
    public Response updatePetById(String petId, String data) throws Exception {
        var uri = new URIBuilder(config.getUrl()).setPathSegments("pet", petId).build();
        var response = Request.Patch(uri)
                .setHeaders(
                        new BasicHeader(HttpHeaders.ACCEPT, config.getContentType().getMimeType()),
                        new BasicHeader(HttpHeaders.CONTENT_TYPE, config.getContentType().getMimeType())
                )
                .connectTimeout(config.getConnectionTimeout())
                .socketTimeout(config.getSocketTimeout())
                .body(new StringEntity(data))
                .execute();
        return response;
    }

    @Override
    public Response deletePetById(String petId, String user, String pass) throws Exception {
        var uri = new URIBuilder(config.getUrl()).setPathSegments("pet", petId).build();
        var cred = getEncryptedText(user, pass);
        var request = Request.Delete(uri)
                .connectTimeout(config.getConnectionTimeout())
                .socketTimeout(config.getSocketTimeout());

        if (StringUtils.isNotBlank(cred)) {
            request.setHeaders(
                    new BasicHeader(HttpHeaders.AUTHORIZATION, "Basic " + cred)
            );
        }

        var response = request.execute();

        return response;
    }

    private String getEncryptedText(String user, String pass) {
        if (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(pass)) {
            var cred = user + ":" + pass;
            return Base64.getEncoder().encodeToString(cred.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }
}
