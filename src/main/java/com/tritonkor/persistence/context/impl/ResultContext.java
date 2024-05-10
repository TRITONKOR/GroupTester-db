package com.tritonkor.persistence.context.impl;

import com.tritonkor.persistence.context.GenericUnitOfWork;
import com.tritonkor.persistence.entity.Result;
import com.tritonkor.persistence.repository.contract.ResultRepository;
import org.springframework.stereotype.Component;

@Component
public class ResultContext extends GenericUnitOfWork<Result> {
    public final ResultRepository repository;

    public ResultContext(ResultRepository repository) {
        super(repository);
        this.repository = repository;
    }
}
