package com.tritonkor.persistence.entity;

import com.tritonkor.persistence.entity.proxy.contract.QuestionProxy;
import com.tritonkor.persistence.entity.proxy.impl.QuestionsProxy;
import java.util.UUID;

/**
 * The {@code Answer} class represents an answer entity with its associated properties.
 */
public class Answer extends Entity implements Comparable<Answer> {

    private final UUID questionId;
    private final QuestionProxy question;
    private String text;
    private Boolean isCorrect;

    public Answer(UUID id, UUID questionId, QuestionProxy question, String text,
            Boolean isCorrect) {
        super(id);
        this.questionId = questionId;
        this.question = question;
        this.text = text;
        this.isCorrect = isCorrect;
    }

    /**
     * Provides a builder for creating instances of {@code Answer}.
     *
     * @return An {@code AnswerBuilderId} instance to start building an {@code Answer}.
     */
    public static AnswerBuilderId builder() {
        return id -> questionId -> question -> text -> isCorrect -> () -> new Answer(id,
                questionId, question, text, isCorrect);
    }

    /**
     * Interface for the {@code Answer} builder to set the ID.
     */
    public interface AnswerBuilderId {

        AnswerBuilderQuestionId id(UUID id);
    }

    public interface AnswerBuilderQuestionId {

        AnswerBuilderQuestion questionId(UUID questionId);
    }

    public interface AnswerBuilderQuestion {

        AnswerBuilderText question(QuestionProxy question);
    }

    /**
     * Interface for the {@code Answer} builder to set the text.
     */
    public interface AnswerBuilderText {

        AnswerBuilderIsCorrect text(String text);
    }

    public interface AnswerBuilderIsCorrect {

        AnswerBuilder isCorrect(Boolean isCorrect);
    }

    /**
     * Interface for the final steps of the {@code Answer} builder.
     */
    public interface AnswerBuilder {

        Answer build();
    }

    /**
     * Gets the text content of the answer.
     *
     * @return The text content of the answer.
     */
    public String getText() {
        return text;
    }

    public Question getQuestionLazy() {
        return question.get(id);
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    @Override
    public int compareTo(Answer o) {
        return this.text.compareTo(o.text);
    }
}
