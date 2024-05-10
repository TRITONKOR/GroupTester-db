package com.tritonkor.persistence.context.impl;

import com.tritonkor.persistence.context.GenericUnitOfWork;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.repository.contract.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserContext extends GenericUnitOfWork<User> {
    public final UserRepository repository;

    protected UserContext(UserRepository repository) {
        super(repository);
        this.repository = repository;
    }
}
