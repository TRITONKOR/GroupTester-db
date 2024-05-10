package com.tritonkor.persistence.entity.proxy.impl;

import com.tritonkor.persistence.entity.Result;
import com.tritonkor.persistence.entity.proxy.contract.Results;
import com.tritonkor.persistence.repository.contract.ResultRepository;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ResultsProxy implements Results {
    private final ApplicationContext applicationContext;

    public ResultsProxy(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Set<Result> get(UUID reportId) {
        ResultRepository resultRepository = applicationContext.getBean(ResultRepository.class);
        Results results = rId -> Collections.unmodifiableSet(resultRepository.findAllByReportId(rId));
        return results.get(reportId);
    }
}
