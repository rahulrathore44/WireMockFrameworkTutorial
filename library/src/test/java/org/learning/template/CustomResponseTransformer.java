package org.learning.template;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.extension.ResponseTransformerV2;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;

import java.util.Iterator;

public class CustomResponseTransformer implements ResponseTransformerV2 {
    @Override
    public Response transform(Response response, ServeEvent serveEvent) {
        return null;
    }

    @Override
    public boolean applyGlobally() {
        return ResponseTransformerV2.super.applyGlobally();
    }

    @Override
    public String getName() {
        return "";
    }
}
