package com.tritonkor.persistence.repository.mapper.impl;

import com.tritonkor.persistence.entity.Report;
import com.tritonkor.persistence.entity.proxy.contract.Results;
import com.tritonkor.persistence.entity.proxy.contract.TestProxy;
import com.tritonkor.persistence.entity.proxy.contract.UserProxy;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.ResultRepository;
import com.tritonkor.persistence.repository.contract.TestRepository;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.tritonkor.persistence.repository.mapper.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ReportRowMapper implements RowMapper<Report> {
    private final UserProxy userProxy;
    private final TestProxy testProxy;
    private final Results results;

    public ReportRowMapper(UserProxy userProxy, TestProxy testProxy, Results results) {
        this.userProxy = userProxy;
        this.testProxy = testProxy;
        this.results = results;
    }

    @Override
    public Report mapRow(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID ownerId = UUID.fromString(rs.getString("owner_id"));
        UUID testId = UUID.fromString(rs.getString("test_id"));

        return Report.builder()
                .id(id)
                .ownerId(ownerId)
                .owner(userProxy)
                .testId(testId)
                .test(testProxy)
                .results(results)
                .createdAt(rs.getTimestamp("create_date").toLocalDateTime())
                .build();
    }
}
