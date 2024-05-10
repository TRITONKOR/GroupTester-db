package com.tritonkor.persistence.context.impl;

import com.tritonkor.persistence.context.GenericUnitOfWork;
import com.tritonkor.persistence.entity.Report;
import com.tritonkor.persistence.repository.contract.ReportRepository;
import org.springframework.stereotype.Component;

@Component
public class ReportContext extends GenericUnitOfWork<Report> {
    public final ReportRepository repository;

    protected ReportContext(ReportRepository repository) {
        super(repository);
        this.repository = repository;
    }
}
