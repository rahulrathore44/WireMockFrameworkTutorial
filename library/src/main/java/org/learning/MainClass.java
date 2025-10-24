package org.learning;

import org.learning.interaction.PetLibrary;
import org.learning.models.CategoryBuilder;
import org.learning.models.PetBuilder;
import org.learning.models.TagBuilder;

import java.util.List;

public class MainClass {
    public static void main(String[] args) {
        var category = CategoryBuilder.create()
                .withId(1)
                .withName("Animal")
                .build();

        var tag = TagBuilder.create()
                .withId(2)
                .withName("Good Dog")
                .build();

        var pet = PetBuilder.create()
                .withId(3)
                .withName("Bruno")
                .withPhotoUrls(List.of("http://localhost:8080/dog.jpg"))
                .withStatus("sold")
                .withCategory(category)
                .withTags(List.of(tag))
                .build();

        var petLibrary = new PetLibrary.PetLibraryBuilder()
                .usingJsonConfiguration("http://localhost:8080")
                .build();

        var output = petLibrary.createPet(pet);
        System.out.println(output);

    }
}
