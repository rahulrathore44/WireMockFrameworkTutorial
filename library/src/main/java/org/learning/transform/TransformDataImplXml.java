package org.learning.transform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.learning.models.Pet;
import org.learning.models.PetWrapper;

import java.util.List;

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

    @Override
    public List<Pet> serializes(String data) {
        try {
            var petWrapper = mapper.readValue(data, new TypeReference<PetWrapper>() {
            });
            return petWrapper.getPets();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String deSerialize(List<Pet> data) {
        var perWrapper = new PetWrapper();
        perWrapper.setPets(data);
        try {
            return mapper.writeValueAsString(perWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
