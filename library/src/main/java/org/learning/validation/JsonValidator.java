package org.learning.validation;

import org.apache.http.HttpHeaders;
import org.apache.http.util.EntityUtils;
import org.learning.dto.DataObject;

public class JsonValidator extends Validator {

    public JsonValidator(DataObject dataObject) {
        super(dataObject);
    }


    @Override
    public String getErrorResponse() {
        try {
            return EntityUtils.toString(dataObject.getResponseBody());
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public void validateResponseHeaders() {
        var headers = dataObject.getResponseHeaders();
        assert headers.get(HttpHeaders.CONTENT_TYPE).equals("application/json") : "Missing the header " + HttpHeaders.CONTENT_TYPE + " from response";
    }

    @Override
    public void validateResponseBody() {
        // Jackson or Gson
    }
}
