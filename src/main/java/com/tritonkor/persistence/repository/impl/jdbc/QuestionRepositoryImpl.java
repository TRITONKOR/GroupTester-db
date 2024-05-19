package com.tritonkor.persistence.repository.impl.jdbc;

import com.tritonkor.persistence.entity.Question;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.entity.filter.QuestionFilterDto;
import com.tritonkor.persistence.entity.filter.UserFilterDto;
import com.tritonkor.persistence.repository.GenericJdbcRepository;
import com.tritonkor.persistence.repository.contract.QuestionRepository;
import com.tritonkor.persistence.repository.contract.TableNames;
import com.tritonkor.persistence.repository.mapper.impl.QuestionRowMapper;
import com.tritonkor.persistence.util.ConnectionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        return findAllWhere(STR."test_id = '\{testId}'");
    }

    @Override
    public Optional<Question> findByText(String text) {
        return findBy("text", text);
    }

    @Override
    public Set<Question> findAll(
            int offset,
            int limit,
            String sortColumn,
            boolean ascending,
            QuestionFilterDto questionFilterDto) {

        HashMap<String, Object> filters = new HashMap<>();

        if (!questionFilterDto.text().isBlank()) {
            filters.put("text", questionFilterDto.text());
        }
        if (Objects.nonNull(questionFilterDto.testId())) {
            filters.put("test_id", questionFilterDto.testId());
        }

        return findAll(offset, limit, sortColumn, ascending, filters);
    }
}
