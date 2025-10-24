package org.learning.interaction;

import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.learning.communication.Communication;
import org.learning.communication.CommunicationImpl;
import org.learning.config.Configuration;
import org.learning.config.DataType;
import org.learning.dto.DataObject;
import org.learning.models.Pet;
import org.learning.transform.TransformData;
import org.learning.transform.TransformDataImplJson;
import org.learning.transform.TransformDataImplXml;
import org.learning.validation.JsonValidator;
import org.learning.validation.Validator;
import org.learning.validation.XMLValidator;

import java.util.List;
import java.util.Objects;

public class PetLibrary {

    private final Communication communication;
    private final TransformData transformData;
    private final DataType dataType;

    private PetLibrary(Communication communication, TransformData transformData, DataType dataType) {
        this.communication = communication;
        this.transformData = transformData;
        this.dataType = dataType;
    }

    public Pet createPet(Pet input) {
        try {

            // Convert the input into JSON or XML using TransformData
            var body = transformData.deSerialize(input);

            // Call the Create API from the communication layer
            var response = communication.create(body);

            // Convert the response in to data transfer object
            var dataObject = DataObject.fromResponse(response);

            // Validate the response using the validation layer (validator)
            validateResponse(dataObject);

            // Convert the response into a Pet Object using TransformData
            return convertObjectFromResponse(dataObject);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Pet> getAllPets() {
        try {

            var response = communication.getAll();
            var dataObject = DataObject.fromResponse(response);
            validateResponse(dataObject);
            return convertObjectsFromResponse(dataObject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void validateResponse(DataObject dataObject) throws Exception {
        Validator validator = switch (dataType) {
            case JSON -> new JsonValidator(dataObject);
            case XML -> new XMLValidator(dataObject);
        };

        validator.validateResponseHeaders();
        validator.validateResponseBody();
        validator.validateStatusCode();
    }

    private Pet convertObjectFromResponse(DataObject dataObject) throws Exception {
        var content = EntityUtils.toString(dataObject.getResponseBody());
        return transformData.serialize(content);
    }

    private List<Pet> convertObjectsFromResponse(DataObject dataObject) throws Exception {
        var content = EntityUtils.toString(dataObject.getResponseBody());
        return transformData.serializes(content);
    }

    public static class PetLibraryBuilder {

        private Communication communication;
        private TransformData transformData;
        private DataType dataType;

        public PetLibraryBuilder withCommunicationObject(Communication communication) {
            this.communication = communication;
            return this;
        }

        public PetLibraryBuilder withTransformData(TransformData transformData) {
            this.transformData = transformData;
            return this;
        }

        public PetLibraryBuilder withDataType(DataType dataType) {
            this.dataType = dataType;
            return this;
        }

        public PetLibrary build() {
            Objects.requireNonNull(this.communication);
            Objects.requireNonNull(this.transformData);
            Objects.requireNonNull(this.dataType);
            return new PetLibrary(this.communication, this.transformData, this.dataType);
        }

        public PetLibraryBuilder usingJsonConfiguration(String url) {
            var configuration = new Configuration.ConfigurationBuilder().withUrl(url)
                    .withContentType(ContentType.APPLICATION_JSON)
                    .withConnectionTimeOut(10000)
                    .withSocketTimeOut(10000)
                    .build();
            var communication = new CommunicationImpl(configuration);
            var transformData = new TransformDataImplJson();
            return new PetLibraryBuilder()
                    .withCommunicationObject(communication)
                    .withDataType(DataType.JSON)
                    .withTransformData(transformData);
        }

        public PetLibraryBuilder usingXmlConfiguration(String url) {
            var configuration = new Configuration.ConfigurationBuilder().withUrl(url)
                    .withContentType(ContentType.APPLICATION_XML)
                    .withConnectionTimeOut(10000)
                    .withSocketTimeOut(10000)
                    .build();
            var communication = new CommunicationImpl(configuration);
            var transformData = new TransformDataImplXml();
            return new PetLibraryBuilder()
                    .withCommunicationObject(communication)
                    .withDataType(DataType.XML)
                    .withTransformData(transformData);
        }

    }
}
