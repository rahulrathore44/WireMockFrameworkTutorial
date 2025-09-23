package org.learning.models;

import java.util.Objects;

public class CategoryBuilder {
    private int id;
    private String name;

    private CategoryBuilder() {
    }

    public CategoryBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public CategoryBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public static CategoryBuilder create() {
        return new CategoryBuilder();
    }

    public Category build() {
        Objects.requireNonNull(name);
        var category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }
}
