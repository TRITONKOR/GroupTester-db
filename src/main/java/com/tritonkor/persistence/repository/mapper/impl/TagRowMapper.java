package com.tritonkor.persistence.repository.mapper.impl;

import com.tritonkor.persistence.entity.proxy.contract.Tests;
import com.tritonkor.persistence.entity.Tag;
import com.tritonkor.persistence.repository.mapper.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TagRowMapper implements RowMapper<Tag> {
    private final Tests tests;

    public TagRowMapper(Tests tests) {
        this.tests = tests;
    }

    @Override
    public Tag mapRow(ResultSet rs) throws SQLException {
        UUID tagId = UUID.fromString(rs.getString("id"));
        return Tag.builder()
                .id(tagId)
                .name(rs.getString("name"))
                .tests(tests)
                .build();
    }
}
