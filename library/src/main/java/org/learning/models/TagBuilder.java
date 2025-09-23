package org.learning.models;

import java.util.Objects;

public class TagBuilder {

    private int id;
    private String name;

    private TagBuilder() {

    }

    public TagBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public TagBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public static TagBuilder create() {
        return new TagBuilder();
    }

    public Tag build() {
        Objects.requireNonNull(name);
        return new Tag(id, name);
    }

}
