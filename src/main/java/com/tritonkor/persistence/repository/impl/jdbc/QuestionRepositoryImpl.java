package com.tritonkor.persistence.repository.impl.jdbc;

import com.tritonkor.persistence.entity.Question;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.repository.GenericJdbcRepository;
import com.tritonkor.persistence.repository.contract.QuestionRepository;
import com.tritonkor.persistence.repository.contract.TableNames;
import com.tritonkor.persistence.repository.mapper.impl.QuestionRowMapper;
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
public class QuestionRepositoryImpl extends GenericJdbcRepository<Question> implements
        QuestionRepository {

    public QuestionRepositoryImpl(ConnectionManager connectionManager,
            QuestionRowMapper rowMapper) {
        super(connectionManager, rowMapper, TableNames.QUESTIONS.getName());
    }

    @Override
    protected Map<String, Object> tableValues(Question question) {
        Map<String, Object> values = new LinkedHashMap<>();

        if (Objects.nonNull(question.getTestId())) {
            values.put("test_id", question.getTestId());
        }
        if (!question.getText().isBlank()) {
            values.put("text", question.getText());
        }

        return values;
    }

    @Override
    public Set<Question> findAllByTestId(UUID testId) {
        return findAllWhere(STR."test_id = \{testId}");
    }
}
