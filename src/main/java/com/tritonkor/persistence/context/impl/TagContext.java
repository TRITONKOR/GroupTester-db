package com.tritonkor.persistence.context.impl;

import com.tritonkor.persistence.context.GenericUnitOfWork;
import com.tritonkor.persistence.entity.Tag;
import com.tritonkor.persistence.repository.contract.TagRepository;
import org.springframework.stereotype.Component;

@Component
public class TagContext extends GenericUnitOfWork<Tag> {

    public final TagRepository repository;

    protected TagContext(TagRepository repository) {
        super(repository);
        this.repository = repository;
    }
}
