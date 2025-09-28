package org.learning.transform;

import org.learning.models.Pet;

import java.util.List;

public interface TransformData {

    Pet serialize(String data);

    String deSerialize(Pet data);

    List<Pet> serializes(String data);

    String deSerialize(List<Pet> data);
}
