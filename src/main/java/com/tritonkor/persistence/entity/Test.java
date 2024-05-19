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
 * It extends the {@code Entity} class and implements {@code Comparable<Test>}.
 */
public class Test extends Entity implements Comparable<Test> {

    private String title;
    private Tags tags;
    private final UUID ownerId;
    private final UserProxy owner;
    private Questions questions;
    private final LocalDateTime createdAt;

    /**
     * Constructs a new {@code Test} instance.
     *
     * @param id the unique identifier for the test
     * @param title the title of the test
     * @param ownerId the unique identifier of the owner
     * @param owner the proxy for the owner
     * @param questions the questions associated with the test
     * @param tags the tags associated with the test
     * @param createdAt the timestamp when the test was created
     */
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

    /**
     * Lazily fetches the user associated with the test.
     *
     * @return The user associated with the test.
     */
    public User getUserLazy() {
        return owner.get(id);
    }

    /**
     * Lazily fetches the questions associated with the test.
     *
     * @return A set of questions associated with the test.
     */
    public Set<Question> getQuestionsLazy() {return questions.get(id);}

    /**
     * Lazily fetches the tags associated with the test.
     *
     * @return A set of tags associated with the test.
     */
    public Set<Tag> getTagsLazy() {
        return tags.get(id);
    }

    /**
     * Gets the unique identifier of the owner of the test.
     *
     * @return The owner's unique identifier.
     */
    public UUID getOwnerId() {
        return ownerId;
    }

    /**
     * Compares this test to another test based on the creation timestamp.
     *
     * @param o the test to compare to
     * @return a negative integer, zero, or a positive integer as this test
     *         is less than, equal to, or greater than the specified test
     */
    @Override
    public int compareTo(Test o) {
        return this.createdAt.compareTo(o.createdAt);
    }

    /**
     * Computes a hash code for this test based on its title.
     *
     * @return A hash code value for this test.
     */
    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
