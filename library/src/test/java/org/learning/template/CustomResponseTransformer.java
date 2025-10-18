package org.learning.template;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.extension.ResponseTransformerV2;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;

import java.util.ArrayList;

public class CustomResponseTransformer implements ResponseTransformerV2 {
    @Override
    public Response transform(Response response, ServeEvent serveEvent) {
        var request = serveEvent.getRequest();
        var filePart = request.getParts().stream().findFirst().get();
        var jsonBody = filePart.getBody().asJson();
        var ids = new ArrayList<String>();
        for (JsonNode node : jsonBody) {
            ids.add(node.get("id").toString());
        }
        return Response.Builder.like(response)
                .but()
                .body(ids.toString())
                .build();
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }

    @Override
    public String getName() {
        return "file-upload-transformer";
    }
}
