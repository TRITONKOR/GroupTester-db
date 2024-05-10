package com.tritonkor.persistence.entity.proxy.impl;

import com.tritonkor.persistence.entity.Report;
import com.tritonkor.persistence.entity.proxy.contract.ReportProxy;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.ReportRepository;
import java.util.UUID;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ReportProxyImpl implements ReportProxy {
    private final ApplicationContext applicationContext;

    public ReportProxyImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Report get(UUID entityId) {
        ReportProxy proxy = (reportId) -> applicationContext.getBean(ReportRepository.class)
                .findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Не вдалось знайти звіт за id"));

        return proxy.get(entityId);
    }
}
