package com.tritonkor.persistence.repository.contract;

import com.tritonkor.persistence.entity.Report;
import com.tritonkor.persistence.repository.Repository;
import java.util.Set;
import java.util.UUID;

public interface ReportRepository extends Repository<Report> {
    Set<Report> findAllByTestId(UUID testId);
    Set<Report> findAllByOwnerId(UUID studentId);
}
