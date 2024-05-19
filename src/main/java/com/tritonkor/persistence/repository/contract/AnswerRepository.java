package com.tritonkor.persistence.repository.contract;

import com.tritonkor.persistence.entity.Answer;
import com.tritonkor.persistence.entity.Question;
import com.tritonkor.persistence.entity.filter.AnswerFilterDto;
import com.tritonkor.persistence.entity.filter.QuestionFilterDto;
import com.tritonkor.persistence.repository.Repository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AnswerRepository extends Repository<Answer> {

    Set<Answer> findAllByQuestionId(UUID questionId);

    Optional<Answer> findByText(String text);

    Set<Answer> findAll(int offset, int limit, String sortColumn, boolean ascending,
            AnswerFilterDto answerFilterDto);
}
