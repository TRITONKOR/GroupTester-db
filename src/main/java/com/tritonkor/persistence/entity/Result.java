package com.tritonkor.persistence.entity;

import com.tritonkor.persistence.entity.proxy.contract.ReportProxy;
import com.tritonkor.persistence.entity.proxy.contract.TestProxy;
import com.tritonkor.persistence.entity.proxy.contract.UserProxy;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The {@code Result} class represents a result of a test for a specific user.
 */
public class Result extends Entity implements Comparable<Result> {

    private final UUID ownerId;
    private final UserProxy owner;
    private final UUID testId;
    private final TestProxy test;
    private final UUID reportId;
    private final ReportProxy report;
    private final Mark mark;
    private final LocalDateTime createdAt;

    public Result(UUID id, UUID ownerId, UserProxy owner, UUID testId, TestProxy test,
            UUID reportId,
            ReportProxy report, Mark mark, LocalDateTime createdAt) {
        super(id);
        this.ownerId = ownerId;
        this.owner = owner;
        this.testId = testId;
        this.test = test;
        this.reportId = reportId;
        this.report = report;
        this.mark = mark;
        this.createdAt = createdAt;
    }

    /**
     * Returns a {@code ResultBuilderId} instance to start building a {@code Result}.
     *
     * @return A {@code ResultBuilderId} instance.
     */
    public static ResultBuilderId builder() {
        return id -> ownerId -> owner -> testId -> test -> reportId -> report -> mark -> createdAt -> () -> new Result(
                id, ownerId, owner, testId, test, reportId, report, mark, createdAt);
    }

    /**
     * Interface for the {@code Result} builder to set the ID.
     */
    public interface ResultBuilderId {

        ResultBuilderOwnerId id(UUID id);
    }

    public interface ResultBuilderOwnerId {

        ResultBuilderOwner ownerId(UUID ownerId);
    }

    public interface ResultBuilderOwner {

        ResultBuilderTestId owner(UserProxy owner);
    }

    public interface ResultBuilderTestId {

        ResultBuilderTest testId(UUID testId);
    }

    public interface ResultBuilderTest {

        ResultBuilderReportId test(TestProxy test);
    }

    public interface ResultBuilderReportId {

        ResultBuilderReport reportId(UUID reportId);
    }

    public interface ResultBuilderReport {

        ResultBuilderGrade report(ReportProxy report);
    }

    /**
     * Interface for the {@code Result} builder to set the grade.
     */
    public interface ResultBuilderGrade {

        ResultBuilderCreatedAt mark(Mark mark);
    }

    /**
     * Interface for the {@code Result} builder to set the creation date.
     */
    public interface ResultBuilderCreatedAt {

        ResultBuilder createdAt(LocalDateTime createdAt);
    }

    /**
     * Interface for the final steps of the {@code Result} builder.
     */
    public interface ResultBuilder {

        Result build();
    }

    public User getOwnerLazy() {
        return owner.get(id);
    }

    public Test getTestLazy() {
        return test.get(id);
    }

    public Report getReportLazy() {
        return report.get(id);
    }

    public Mark getMark() {
        return mark;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public UUID getTestId() {
        return testId;
    }

    public UUID getReportId() {
        return reportId;
    }

    /**
     * Gets the creation timestamp of the result.
     *
     * @return The creation timestamp.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public int compareTo(Result o) {
        return this.createdAt.compareTo(o.createdAt);
    }
}
