package com.github.mohrezal.api.support.builders;

import com.github.mohrezal.api.domains.categories.models.Category;
import java.util.UUID;

public class CategoryBuilder {

    private UUID id;
    private String name = "Test category";
    private String slug = "test-category";
    private String description = "Test description";
    private Category parent = null;

    public static CategoryBuilder aCategory() {
        return new CategoryBuilder();
    }

    public CategoryBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public CategoryBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CategoryBuilder withSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public CategoryBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public Category build() {
        var category = Category.builder().name(name).slug(slug).description(description).build();

        if (id != null) {
            category.setId(id);
        }

        return category;
    }
}
