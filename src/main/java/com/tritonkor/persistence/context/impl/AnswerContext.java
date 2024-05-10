package com.tritonkor.persistence.context.impl;

import com.tritonkor.persistence.context.GenericUnitOfWork;
import com.tritonkor.persistence.entity.Answer;
import com.tritonkor.persistence.repository.contract.AnswerRepository;
import org.springframework.stereotype.Component;

@Component
public class AnswerContext extends GenericUnitOfWork<Answer> {
    public final AnswerRepository repository;

    protected AnswerContext(AnswerRepository repository) {
        super(repository);
        this.repository = repository;
    }
}
