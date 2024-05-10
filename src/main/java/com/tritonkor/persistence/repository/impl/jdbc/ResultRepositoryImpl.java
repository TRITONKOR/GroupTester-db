package com.tritonkor.persistence.repository.impl.jdbc;

import com.tritonkor.persistence.entity.Result;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.repository.GenericJdbcRepository;
import com.tritonkor.persistence.repository.contract.ResultRepository;
import com.tritonkor.persistence.repository.contract.TableNames;
import com.tritonkor.persistence.repository.mapper.impl.ResultRowMapper;
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
public class ResultRepositoryImpl extends GenericJdbcRepository<Result> implements
        ResultRepository {

    public ResultRepositoryImpl(ConnectionManager connectionManager,
            ResultRowMapper rowMapper) {
        super(connectionManager, rowMapper, TableNames.RESULTS.getName());
    }

    @Override
    protected Map<String, Object> tableValues(Result result) {
        Map<String, Object> values = new LinkedHashMap<>();

        if (Objects.nonNull(result.getTestId())) {
            values.put("test_id", result.getTestId());
        }
        if (Objects.nonNull(result.getOwnerId())) {
            values.put("owner_id", result.getOwnerId());
        }
        if (Objects.nonNull(result.getReportId())) {
            values.put("report_id", result.getReportId());
        }
        if (Objects.nonNull(result.getMark())) {
            values.put("mark", result.getMark().getGrade());
        }
        return values;
    }

    @Override
    public Set<Result> findAllByReportId(UUID reportId) {
        return findAllWhere(STR."report_id = \{reportId}");
    }

    @Override
    public Set<Result> findAllByTestId(UUID testId) {
        return findAllWhere(STR."test_id = \{testId}");
    }

    @Override
    public Set<Result> findAllByOwnerId(UUID studentId) {
        return findAllWhere(STR."owner_id = \{studentId}");
    }
}
