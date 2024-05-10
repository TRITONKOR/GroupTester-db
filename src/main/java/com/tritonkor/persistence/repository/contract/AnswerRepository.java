package com.tritonkor.persistence.repository.contract;

import com.tritonkor.persistence.entity.Answer;
import com.tritonkor.persistence.repository.Repository;
import java.util.Set;
import java.util.UUID;

public interface AnswerRepository extends Repository<Answer> {

    Set<Answer> findAllByQuestionId(UUID questionId);
}
