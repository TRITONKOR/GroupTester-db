package com.tritonkor.persistence.entity;

import com.tritonkor.persistence.entity.proxy.contract.Questions;
import com.tritonkor.persistence.entity.proxy.contract.Tags;
import com.tritonkor.persistence.entity.proxy.contract.UserProxy;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.Objects;
import java.util.UUID;

/**
 * The {@code Test} class represents a test in the system.
 */
public class Test extends Entity implements Comparable<Test> {

    private String title;
    private Tags tags;
    private final UUID ownerId;
    private final UserProxy owner;
    private Questions questions;
    private final LocalDateTime createdAt;

    public Test(UUID id, String title, UUID ownerId, UserProxy owner,
            Questions questions, Tags tags,
            LocalDateTime createdAt) {
        super(id);
        this.title = title;
        this.tags = tags;
        this.ownerId = ownerId;
        this.owner = owner;
        this.questions = questions;
        this.createdAt = createdAt;
    }

    /**
     * Returns a {@code TestBuilderId} instance to start building a {@code Test}.
     *
     * @return A {@code TestBuilderId} instance.
     */
    public static TestBuilderId builder() {
        return id -> title -> ownerId -> owner -> questions -> tags -> createdAt -> () -> new Test(
                id, title,
                ownerId, owner, questions, tags, createdAt);
    }

    /**
     * Interface for the {@code Test} builder to set the ID.
     */
    public interface TestBuilderId {

        TestBuilderTitle id(UUID id);
    }

    /**
     * Interface for the {@code Test} builder to set the title.
     */
    public interface TestBuilderTitle {

        TestBuilderOwnerId title(String title);
    }


    public interface TestBuilderOwnerId {

        TestBuilderOwner ownerId(UUID ownerId);
    }

    public interface TestBuilderOwner {

        TestBuilderQuestions owner(UserProxy owner);
    }

    public interface TestBuilderQuestions {

        TestBuilderTags questions(Questions questions);
    }

    public interface TestBuilderTags {

        TestBuilderCreatedAt tags(Tags tags);
    }

    /**
     * Interface for the {@code Test} builder to set the creation date.
     */
    public interface TestBuilderCreatedAt {

        TestBuilder createdAt(LocalDateTime createdAt);
    }

    /**
     * Interface for the final steps of the {@code Test} builder.
     */
    public interface TestBuilder {

        Test build();
    }

    /**
     * Gets the title of the test.
     *
     * @return The test's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the creation timestamp of the test.
     *
     * @return The creation timestamp.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getUserLazy() {
        return owner.get(id);
    }

    public Set<Question> getQuestionsLazy() {return questions.get(id);}

    public Set<Tag> getTagsLazy() {
        return tags.get(id);
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    @Override
    public int compareTo(Test o) {
        return this.createdAt.compareTo(o.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
