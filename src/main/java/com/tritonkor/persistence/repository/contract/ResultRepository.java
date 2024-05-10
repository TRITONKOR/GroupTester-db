package com.tritonkor.persistence.repository.contract;

import com.tritonkor.persistence.entity.Result;
import com.tritonkor.persistence.repository.Repository;
import java.util.Set;
import java.util.UUID;

public interface ResultRepository extends Repository<Result> {

    Set<Result> findAllByReportId(UUID reportId);
    Set<Result> findAllByTestId(UUID testId);
    Set<Result> findAllByOwnerId(UUID studentId);
}
