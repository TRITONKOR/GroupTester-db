package com.tritonkor.persistence.repository.mapper.impl;

import com.tritonkor.persistence.entity.Question;
import com.tritonkor.persistence.entity.proxy.contract.Answers;
import com.tritonkor.persistence.entity.proxy.contract.TestProxy;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.AnswerRepository;
import com.tritonkor.persistence.repository.contract.TestRepository;
import com.tritonkor.persistence.repository.mapper.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class QuestionRowMapper implements RowMapper<Question> {
    private final TestProxy testProxy;
    private final Answers answers;

    public QuestionRowMapper(Answers answers,TestProxy testProxy) {
        this.answers = answers;
        this.testProxy = testProxy;
    }

    @Override
    public Question mapRow(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID testId = UUID.fromString(rs.getString("test_id"));

        return Question.builder()
                .id(id)
                .text(rs.getString("text"))
                .answers(answers)
                .testId(testId)
                .test(testProxy)
                .build();
    }
}
