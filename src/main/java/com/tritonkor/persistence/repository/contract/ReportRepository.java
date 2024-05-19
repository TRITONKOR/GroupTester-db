package com.tritonkor.persistence.repository.contract;

import com.tritonkor.persistence.entity.Report;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.filter.ReportFilterDto;
import com.tritonkor.persistence.entity.filter.TestFilterDto;
import com.tritonkor.persistence.repository.Repository;
import java.util.Set;
import java.util.UUID;

public interface ReportRepository extends Repository<Report> {
    Set<Report> findAllByTestId(UUID testId);
    Set<Report> findAllByOwnerId(UUID studentId);

    Set<Report> findAll(int offset,
            int limit,
            String sortColumn,
            boolean ascending,
            ReportFilterDto reportFilterDto);
}
