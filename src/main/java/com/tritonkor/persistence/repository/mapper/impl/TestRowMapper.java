package com.tritonkor.persistence.repository.mapper.impl;

import com.tritonkor.persistence.entity.proxy.contract.Questions;
import com.tritonkor.persistence.entity.proxy.contract.Tags;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.proxy.contract.UserProxy;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.tritonkor.persistence.repository.mapper.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TestRowMapper implements RowMapper<Test> {

    private final UserProxy userProxy;
    private final Questions questions;
    private final Tags tags;

    public TestRowMapper(UserProxy userProxy, Questions questions, Tags tags) {
        this.userProxy = userProxy;
        this.questions = questions;
        this.tags = tags;
    }

    @Override
    public Test mapRow(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID ownerId = UUID.fromString(rs.getString("owner_id"));

        return Test.builder()
                .id(id)
                .title(rs.getString("title"))
                .ownerId(ownerId)
                .owner(userProxy)
                .questions(questions)
                .tags(tags)
                .createdAt(rs.getTimestamp("create_date").toLocalDateTime()).build();
    }
}
