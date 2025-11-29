package org.learning.interaction;

import org.learning.models.CategoryBuilder;
import org.learning.models.PetBuilder;
import org.learning.models.TagBuilder;

import java.util.List;
import java.util.Random;

public class Assignment_9_Pet_Library_Interaction {

    private static final Random random = new Random();

    public static void main(String[] args) {
        int petId = random.nextInt(1, 100);
        var category = CategoryBuilder.create()
                .withId(1)
                .withName("Animal")
                .build();

        var tag = TagBuilder.create()
                .withId(2)
                .withName("Good Dog")
                .build();

        var pet = PetBuilder.create()
                .withId(petId)
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
        var listOfPets = petLibrary.getAllPets();
        System.out.println(listOfPets);
    }
}
