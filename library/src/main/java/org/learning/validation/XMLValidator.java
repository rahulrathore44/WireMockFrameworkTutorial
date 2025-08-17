package org.learning.validation;

import org.apache.http.HttpHeaders;
import org.apache.http.util.EntityUtils;
import org.learning.dto.DataObject;

public class XMLValidator extends Validator {

    public final DataObject dataObject;

    public XMLValidator(DataObject dataObject) {
        super(dataObject);
        this.dataObject = dataObject;
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
        assert headers.get(HttpHeaders.CONTENT_TYPE).equals("application/xml") : "Missing the header " + HttpHeaders.CONTENT_TYPE + " from response";

    }

    @Override
    public void validateResponseBody() {
        // parse the xml and validate it

    }
}
