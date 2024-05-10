package com.tritonkor.persistence.entity.proxy.impl;

import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.proxy.contract.Tests;
import com.tritonkor.persistence.repository.contract.TagRepository;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TestsProxy implements Tests {
    private final ApplicationContext applicationContext;

    public TestsProxy(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Set<Test> get(UUID tagId) {
        TagRepository tagRepository = applicationContext.getBean(TagRepository.class);
        Tests tests = tId -> Collections.unmodifiableSet(tagRepository.findAllTests(tId));
        return tests.get(tagId);
    }
}
