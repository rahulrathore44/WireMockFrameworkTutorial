package org.learning.transform;

import org.learning.models.Pet;

public interface TransformData {

    Pet serialize(String data);

    String deSerialize(Pet data);
}
