package com.tritonkor.persistence.repository.impl.jdbc;

import com.tritonkor.persistence.entity.Report;
import com.tritonkor.persistence.entity.Result;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.filter.ReportFilterDto;
import com.tritonkor.persistence.entity.filter.TestFilterDto;
import com.tritonkor.persistence.repository.GenericJdbcRepository;
import com.tritonkor.persistence.repository.contract.ReportRepository;
import com.tritonkor.persistence.repository.contract.TableNames;
import com.tritonkor.persistence.repository.mapper.impl.ReportRowMapper;
import com.tritonkor.persistence.util.ConnectionManager;
import java.util.ArrayList;
import java.util.HashMap;
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
        values.put("create_date", report.getCreatedAt());

        return values;
    }

    @Override
    public Set<Report> findAllByTestId(UUID testId) {
        return findAllWhere(STR."test_id = '\{testId}'");
    }

    @Override
    public Set<Report> findAllByOwnerId(UUID studentId) {
        return findAllWhere(STR."owner_id = '\{studentId}'");
    }

    @Override
    public Set<Report> findAll(int offset, int limit, String sortColumn, boolean ascending,
            ReportFilterDto reportFilterDto) {
        return findAll(offset, limit, sortColumn, ascending, reportFilterDto, "");
    }

    private Set<Report> findAll(int offset, int limit, String sortColumn, boolean ascending,
            ReportFilterDto reportFilterDto, String wherePrefix) {
        StringBuilder where = new StringBuilder(STR."\{wherePrefix} ");
        HashMap<String, Object> filters = new HashMap<>();

        // Додавання фільтрів до where-умови
        if (Objects.nonNull(reportFilterDto.ownerId())) {
            filters.put("owner_id", reportFilterDto.ownerId());
        }
        if (Objects.nonNull(reportFilterDto.testId())) {
            filters.put("test_id", reportFilterDto.testId());
        }

        // Фільтр по created_at
        if (Objects.nonNull(reportFilterDto.createdAtStart())
                && Objects.nonNull(reportFilterDto.createdAtEnd())) {
            where.append(
                    STR."create_date BETWEEN '\{reportFilterDto.createdAtStart()}' AND '\{reportFilterDto.createdAtEnd()}' ");
        } else if (Objects.nonNull(reportFilterDto.createdAtStart())) {
            where.append(STR."create_date >= '\{reportFilterDto.createdAtStart()}' ");
        } else if (Objects.nonNull(reportFilterDto.createdAtEnd())) {
            where.append(STR."create_date <= '\{reportFilterDto.createdAtEnd()}' ");
        }


        return findAll(offset, limit, sortColumn, ascending, filters, where.toString());
    }
}
