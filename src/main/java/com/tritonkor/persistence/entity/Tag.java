package com.tritonkor.persistence.entity;

import com.tritonkor.persistence.entity.proxy.contract.Tests;
import java.util.Set;
import java.util.UUID;

public class Tag extends Entity implements Comparable<Tag>{

    private final String name;
    private Tests tests;

    public Tag(UUID id, String name, Tests tests) {
        super(id);
        this.name = name;
        this.tests = tests;
    }

    public static TagBuilderId builder() {
        return id -> name -> tests -> () -> new Tag(id, name, tests);
    }

    public interface TagBuilderId {
        TagBuilderName id(UUID id);
    }

    public interface TagBuilderName {
        TagBuilderTests name(String username);
    }

    public interface TagBuilderTests {
        TagBuilder tests(Tests tests);
    }

    public interface TagBuilder {
        Tag build();
    }

    public String getName() {
        return name;
    }

    public Tests getTests() {
        return tests;
    }

    public Set<Test> getTestsLazy() {
        return tests.get(id);
    }

    @Override
    public int compareTo(Tag o) {
        return this.name.compareTo(o.name);
    }
}
