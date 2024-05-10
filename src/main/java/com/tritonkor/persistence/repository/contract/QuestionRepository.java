package com.tritonkor.persistence.repository.contract;

import com.tritonkor.persistence.entity.Question;
import com.tritonkor.persistence.repository.Repository;
import java.util.Set;
import java.util.UUID;

public interface QuestionRepository extends Repository<Question> {

    Set<Question> findAllByTestId(UUID testId);
}
