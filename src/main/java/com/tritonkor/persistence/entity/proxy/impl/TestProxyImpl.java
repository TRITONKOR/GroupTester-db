package com.tritonkor.persistence.entity.proxy.impl;

import com.tritonkor.persistence.entity.Report;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.proxy.contract.ReportProxy;
import com.tritonkor.persistence.entity.proxy.contract.TestProxy;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.ReportRepository;
import com.tritonkor.persistence.repository.contract.TestRepository;
import java.util.UUID;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TestProxyImpl implements TestProxy {
    private final ApplicationContext applicationContext;

    public TestProxyImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Test get(UUID entityId) {
        TestProxy proxy = (testId) -> applicationContext.getBean(TestRepository.class)
                .findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Не вдалось знайти тест за id"));

        return proxy.get(entityId);
    }
}
