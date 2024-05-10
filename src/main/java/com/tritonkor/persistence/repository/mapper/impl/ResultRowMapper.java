package com.tritonkor.persistence.repository.mapper.impl;

import com.tritonkor.persistence.entity.Mark;
import com.tritonkor.persistence.entity.Result;
import com.tritonkor.persistence.entity.proxy.contract.ReportProxy;
import com.tritonkor.persistence.entity.proxy.contract.TestProxy;
import com.tritonkor.persistence.entity.proxy.contract.UserProxy;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.ReportRepository;
import com.tritonkor.persistence.repository.contract.TestRepository;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.tritonkor.persistence.repository.mapper.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ResultRowMapper implements RowMapper<Result> {
    private final UserProxy userProxy;
    private final TestProxy testProxy;
    private final ReportProxy reportProxy;

    public ResultRowMapper(UserProxy userProxy, TestProxy testProxy,
            ReportProxy reportProxy) {
        this.userProxy = userProxy;
        this.testProxy = testProxy;
        this.reportProxy = reportProxy;
    }

    @Override
    public Result mapRow(ResultSet rs) throws SQLException {
        UUID resultId = UUID.fromString(rs.getString("id"));
        UUID ownerId = UUID.fromString(rs.getString("owner_id"));
        UUID testId = UUID.fromString(rs.getString("test_id"));
        UUID reportId = UUID.fromString(rs.getString("report_id"));

        return Result.builder()
                .id(resultId)
                .ownerId(ownerId)
                .owner(userProxy)
                .testId(testId)
                .test(testProxy)
                .reportId(reportId)
                .report(reportProxy)
                .mark(new Mark(rs.getInt("mark")))
                .createdAt(rs.getTimestamp("create_date").toLocalDateTime())
                .build();
    }
}
