package com.tritonkor.persistence.entity.proxy.impl;

import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.proxy.contract.TestProxy;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.TestRepository;
import java.util.UUID;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@code TestProxy} interface to handle lazy loading of {@code Test} entities.
 */
@Component
public class TestProxyImpl implements TestProxy {
    private final ApplicationContext applicationContext;

    /**
     * Constructs a new {@code TestProxyImpl} instance with the specified application context.
     *
     * @param applicationContext the application context used to retrieve beans
     */
    public TestProxyImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Retrieves a {@code Test} entity by its unique identifier.
     *
     * @param entityId the unique identifier of the test
     * @return the {@code Test} entity
     * @throws EntityNotFoundException if the test with the specified ID is not found
     */
    @Override
    public Test get(UUID entityId) {
        TestProxy proxy = (testId) -> applicationContext.getBean(TestRepository.class)
                .findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Не вдалось знайти тест за id"));

        return proxy.get(entityId);
    }
}
