package com.tritonkor.persistence.repository.impl.jdbc;

import com.tritonkor.persistence.entity.Answer;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.repository.GenericJdbcRepository;
import com.tritonkor.persistence.repository.contract.AnswerRepository;
import com.tritonkor.persistence.repository.contract.TableNames;
import com.tritonkor.persistence.repository.mapper.impl.AnswerRowMapper;
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
public class AnswerRepositoryImpl extends GenericJdbcRepository<Answer> implements
        AnswerRepository {

    public AnswerRepositoryImpl(ConnectionManager connectionManager,
            AnswerRowMapper rowMapper) {
        super(connectionManager, rowMapper, TableNames.ANSWERS.getName());
    }

    @Override
    protected Map<String, Object> tableValues(Answer answer) {
        Map<String, Object> values = new LinkedHashMap<>();

        if (Objects.nonNull(answer.getQuestionId())) {
            values.put("question_id", answer.getQuestionId());
        }
        if (!answer.getText().isBlank()) {
            values.put("text", answer.getText());
        }
        if (Objects.nonNull(answer.getCorrect())) {
            values.put("is_correct", answer.getCorrect());
        }

        return values;
    }

    @Override
    public Set<Answer> findAllByQuestionId(UUID questionId) {
        return findAllWhere(STR."question_id = \{questionId}");
    }


}
