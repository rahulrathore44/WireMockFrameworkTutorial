package org.learning.models;

import java.util.List;
import java.util.Objects;

public class PetBuilder {

    private int id;
    private String name;
    private Category category;
    private List<String> photoUrls;
    private List<Tag> tags;
    private String status;

    private PetBuilder() {}

    public PetBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public PetBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PetBuilder withCategory(Category category) {
        this.category = category;
        return this;
    }

    public PetBuilder withPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
        return this;
    }

    public PetBuilder withTags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public PetBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public static PetBuilder create() {
        return new PetBuilder();
    }

    public Pet build() {
        Objects.requireNonNull(name);
        Objects.requireNonNull(category);
        var pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setCategory(category);
        pet.setPhotoUrls(photoUrls);
        pet.setTags(tags);
        pet.setStatus(status);
        return pet;
    }

}
