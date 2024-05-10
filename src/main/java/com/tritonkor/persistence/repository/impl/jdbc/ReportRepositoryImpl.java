package com.tritonkor.persistence.repository.impl.jdbc;

import com.tritonkor.persistence.entity.Report;
import com.tritonkor.persistence.entity.Result;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.repository.GenericJdbcRepository;
import com.tritonkor.persistence.repository.contract.ReportRepository;
import com.tritonkor.persistence.repository.contract.TableNames;
import com.tritonkor.persistence.repository.mapper.impl.ReportRowMapper;
import com.tritonkor.persistence.util.ConnectionManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ReportRepositoryImpl extends GenericJdbcRepository<Report> implements
        ReportRepository {

    public ReportRepositoryImpl(ConnectionManager connectionManager,
            ReportRowMapper rowMapper) {
        super(connectionManager, rowMapper, TableNames.REPORTS.getName());
    }

    @Override
    protected Map<String, Object> tableValues(Report report) {
        Map<String, Object> values = new LinkedHashMap<>();

        if (Objects.nonNull(report.getOwnerId())) {
            values.put("owner_id", report.getOwnerId());
        }
        if (Objects.nonNull(report.getTestId())) {
            values.put("test_id", report.getTestId());
        }

        return values;
    }

    @Override
    public Set<Report> findAllByTestId(UUID testId) {
        return findAllWhere(STR."test_id = \{testId}");
    }

    @Override
    public Set<Report> findAllByOwnerId(UUID studentId) {
        return findAllWhere(STR."owner_id = \{studentId}");
    }
}
