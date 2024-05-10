package com.tritonkor.persistence.context.impl;

import com.tritonkor.persistence.context.GenericUnitOfWork;
import com.tritonkor.persistence.entity.Question;
import com.tritonkor.persistence.repository.contract.QuestionRepository;
import org.springframework.stereotype.Component;

@Component
public class QuestionContext extends GenericUnitOfWork<Question> {
    public final QuestionRepository repository;

    protected QuestionContext(QuestionRepository repository) {
        super(repository);
        this.repository = repository;
    }
}
