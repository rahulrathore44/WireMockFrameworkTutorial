package org.learning.transform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.learning.models.Pet;

import java.util.List;

public class TransformDataImplJson implements TransformData {

    private final ObjectMapper mapper;

    public TransformDataImplJson() {
        mapper = new ObjectMapper();
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

    @Override
    public List<Pet> serializes(String data) {
        try {
            return mapper.readValue(data, new TypeReference<List<Pet>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String deSerialize(List<Pet> data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
