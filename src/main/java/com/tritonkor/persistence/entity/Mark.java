package com.tritonkor.persistence.entity;

/**
 * The {@code Grade} class represents a grade with its associated properties.
 */
public class Mark {

    private final int MAX_GRADE = 100;

    private int grade;

    /**
     * Constructs a {@code Grade} instance with the specified grade value.
     *
     * @param grade The grade value.
     */
    public Mark(int grade) {
        this.grade = grade;
    }

    /**
     * Gets the grade value.
     *
     * @return The grade value.
     */
    public int getGrade() {
        return grade;
    }

    /**
     * Sets the grade value.
     *
     * @param grade The new grade value.
     */
    public void setGrade(int grade) {
        this.grade = grade;
    }

    /**
     * Returns a string representation of the grade.
     *
     * @return A string representation of the grade.
     */
    @Override
    public String toString() {
        return "Grade:" + grade + "/" + MAX_GRADE;
    }
}
