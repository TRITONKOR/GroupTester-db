package com.tritonkor.persistence.entity;

import com.tritonkor.persistence.entity.proxy.contract.Results;
import com.tritonkor.persistence.entity.proxy.contract.TestProxy;
import com.tritonkor.persistence.entity.proxy.contract.UserProxy;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * The {@code Report} class represents a report containing information about test results for a
 * group.
 */
public class Report extends Entity implements Comparable<Report> {
    private final UUID ownerId;
    private final UserProxy owner;
    private final UUID testId;
    private final TestProxy test;
    private final Results results;
    private final LocalDateTime createdAt;

    public Report(UUID id, UUID ownerId, UserProxy owner, UUID testId, TestProxy test, Results results,
            LocalDateTime createdAt) {
        super(id);
        this.ownerId = ownerId;
        this.owner = owner;
        this.testId = testId;
        this.test = test;
        this.results = results;
        this.createdAt = createdAt;
    }

    /**
     * Returns a {@code ReportBuilderId} instance to start building a {@code Report}.
     *
     * @return A {@code ReportBuilderId} instance.
     */
    public static ReportBuilderId builder() {
        return id -> ownerId -> owner -> testId -> test -> results -> createdAt -> () -> new Report(
                id, ownerId, owner, testId, test, results, createdAt);
    }

    /**
     * Interface for the {@code Report} builder to set the ID.
     */
    public interface ReportBuilderId {

        ReportBuilderOwnerId id(UUID id);
    }

    public interface ReportBuilderOwnerId {

        ReportBuilderOwner ownerId(UUID ownerId);
    }

    public interface ReportBuilderOwner {

        ReportBuilderTestId owner(UserProxy owner);
    }

    public interface ReportBuilderTestId {

        ReportBuilderTest testId(UUID testId);
    }

    /**
     * Interface for the {@code Report} builder to set the test .
     */
    public interface ReportBuilderTest {

        ReportBuilderResults test(TestProxy test);
    }

    public interface ReportBuilderResults {

        ReportBuilderCreatedAt results(Results results);
    }

    /**
     * Interface for the {@code Report} builder to set the creation date.
     */
    public interface ReportBuilderCreatedAt {

        ReportBuilder createdAt(LocalDateTime createdAt);
    }

    /**
     * Interface for the final steps of the {@code Report} builder.
     */
    public interface ReportBuilder {

        Report build();
    }

    /**
     * Gets the creation timestamp of the report.
     *
     * @return The creation timestamp.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getOwnerLazy() {
        return owner.get(id);
    }

    public Test getTestLazy() {
        return test.get(id);
    }

    public Set<Result> getResultsLazy() { return results.get(id);}

    public UUID getOwnerId() {
        return ownerId;
    }

    public UUID getTestId() {
        return testId;
    }

    @Override
    public int compareTo(Report o) {
        return this.createdAt.compareTo(o.createdAt);
    }

}
