package com.tritonkor.persistence.entity;

import com.tritonkor.persistence.entity.proxy.contract.Answers;
import com.tritonkor.persistence.entity.proxy.contract.TestProxy;
import java.util.Set;
import java.util.UUID;

/**
 * The {@code Question} class represents a question with its associated properties.
 */
public class Question extends Entity implements Comparable<Question>{

    private String text;
    private Answers answers;
    private final UUID testId;
    private final TestProxy test;

    public Question(UUID id, String text, Answers answers, UUID testId, TestProxy test) {
        super(id);
        this.text = text;
        this.answers = answers;
        this.testId = testId;
        this.test = test;
    }

    /**
     * Returns a {@code QuestionBuilderId} instance to start building a {@code Question}.
     *
     * @return A {@code QuestionBuilderId} instance.
     */
    public static QuestionBuilderId builder() {
        return id -> answers -> testId -> test -> content -> () -> new Question(id, answers, testId, test,
                content);
    }

    /**
     * Interface for the {@code Question} builder to set the ID.
     */
    public interface QuestionBuilderId {

        QuestionBuilderText id(UUID id);
    }

    public interface QuestionBuilderText {

        QuestionBuilderAnswers text(String text);
    }

    public interface QuestionBuilderAnswers {

        QuestionBuilderTestId answers(Answers answers);
    }

    public interface QuestionBuilderTestId {

        QuestionBuilderTest testId(UUID testId);
    }

    public interface QuestionBuilderTest {

        QuestionBuilder test(TestProxy test);
    }

    /**
     * Interface for the final steps of the {@code Question} builder.
     */
    public interface QuestionBuilder {

        Question build();
    }

    /**
     * Gets the text of the question.
     *
     * @return The question text.
     */
    public String getText() {
        return text;
    }

    public Set<Answer> getAnswersLazy() {
        return answers.get(id);
    }

    public Test getTestLazy() {
        return test.get(id);
    }

    public UUID getTestId() {
        return testId;
    }

    @Override
    public int compareTo(Question o) {
        return this.text.compareTo(o.text);
    }
}
