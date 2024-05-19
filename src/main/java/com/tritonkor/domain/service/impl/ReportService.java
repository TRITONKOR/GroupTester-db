package com.tritonkor.domain.service.impl;

import com.tritonkor.domain.dto.ReportStoreDto;
import com.tritonkor.domain.dto.ReportUpdateDto;
import com.tritonkor.domain.exception.ValidationException;
import com.tritonkor.persistence.context.factory.PersistenceContext;
import com.tritonkor.persistence.context.impl.ReportContext;
import com.tritonkor.persistence.entity.Report;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.ReportRepository;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final ReportContext reportContext;
    private final ReportRepository reportRepository;
    private final AuthorizeService authorizeService;
    private final Validator validator;

    public ReportService(PersistenceContext persistenceContext, AuthorizeService authorizeService,
            Validator validator) {
        this.reportContext = persistenceContext.reports;
        this.reportRepository = persistenceContext.reports.repository;
        this.authorizeService = authorizeService;
        this.validator = validator;
    }

    public Report findById(UUID id) {
        return reportContext.repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не вдалось знайти питання"));
    }

    public Set<Report> findAll() {
        return new TreeSet<>(reportRepository.findAll());
    }


    public Set<Report> findAllByTestId(UUID testId) {
        return new TreeSet<>(reportContext.repository.findAllByTestId(testId));
    }

    public Set<Report> findAllByUserId(UUID userId) {
        return new TreeSet<>(reportContext.repository.findAllByOwnerId(userId));
    }

    public long count() {
        return reportRepository.count();
    }

    public Report create(ReportStoreDto reportStoreDto) {
        var violations = validator.validate(reportStoreDto);
        if (!violations.isEmpty()) {
            throw ValidationException.create("збереженні звіту", violations);
        }

        Report report = Report.builder()
                .id(null)
                .ownerId(reportStoreDto.ownerId())
                .owner(null)
                .testId(reportStoreDto.testId())
                .test(null)
                .results(null)
                .createdAt(null)
                .build();

        reportContext.registerNew(report);
        reportContext.commit();
        return reportContext.getEntity();
    }

    public Report update(ReportUpdateDto reportUpdateDto) {
        var violations = validator.validate(reportUpdateDto);
        if (!violations.isEmpty()) {
            throw ValidationException.create("оновленні звіту", violations);
        }

        Report report = Report.builder()
                .id(reportUpdateDto.id())
                .ownerId(reportUpdateDto.ownerId())
                .owner(null)
                .testId(reportUpdateDto.testId())
                .test(null)
                .results(null)
                .createdAt(null)
                .build();

        reportContext.registerModified(report);
        reportContext.commit();
        return reportContext.getEntity();
    }

    public boolean delete(UUID id) {
        Report report = findById(id);

        return reportContext.repository.delete(report.getId());
    }
}
