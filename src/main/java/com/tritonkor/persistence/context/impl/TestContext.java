package com.tritonkor.persistence.context.impl;

import com.tritonkor.persistence.context.GenericUnitOfWork;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.repository.contract.TestRepository;
import org.springframework.stereotype.Component;

@Component
public class TestContext extends GenericUnitOfWork<Test> {
    public final TestRepository repository;

    protected TestContext(TestRepository repository) {
        super(repository);
        this.repository = repository;
    }
}
