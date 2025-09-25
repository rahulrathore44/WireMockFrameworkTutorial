package org.learning.transform;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.learning.models.Pet;

public class TransformDataImplXml implements TransformData {

    private final XmlMapper mapper;

    public TransformDataImplXml() {
        mapper = new XmlMapper();
    }

    @Override
    public Pet serialize(String data) {
        try {
            return mapper.readValue(data, Pet.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String deSerialize(Pet data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
