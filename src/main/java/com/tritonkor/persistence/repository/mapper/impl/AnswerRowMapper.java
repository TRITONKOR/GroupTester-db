package com.tritonkor.persistence.repository.mapper.impl;

import com.tritonkor.persistence.entity.Answer;
import com.tritonkor.persistence.entity.proxy.contract.QuestionProxy;
import com.tritonkor.persistence.entity.proxy.contract.UserProxy;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.QuestionRepository;
import com.tritonkor.persistence.repository.mapper.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AnswerRowMapper implements RowMapper<Answer> {

    private final QuestionProxy questionProxy;

    public AnswerRowMapper(QuestionProxy questionProxy) {
        this.questionProxy = questionProxy;
    }

    @Override
    public Answer mapRow(ResultSet rs) throws SQLException {
        UUID answerId = UUID.fromString(rs.getString("id"));
        UUID questionId = UUID.fromString(rs.getString("question_id"));

        return Answer.builder()
                .id(answerId)
                .questionId(questionId)
                .question(questionProxy)
                .text(rs.getString("text"))
                .isCorrect(rs.getBoolean("is_correct"))
                .build();
    }
}
