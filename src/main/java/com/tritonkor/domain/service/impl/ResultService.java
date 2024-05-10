package com.tritonkor.domain.service.impl;

import com.tritonkor.domain.dto.ReportStoreDto;
import com.tritonkor.domain.dto.ResultStoreDto;
import com.tritonkor.domain.exception.ValidationException;
import com.tritonkor.persistence.context.factory.PersistenceContext;
import com.tritonkor.persistence.context.impl.ReportContext;
import com.tritonkor.persistence.context.impl.ResultContext;
import com.tritonkor.persistence.entity.Report;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.ReportRepository;
import com.tritonkor.persistence.repository.contract.ResultRepository;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import com.tritonkor.persistence.entity.Result;
import org.springframework.stereotype.Service;

@Service
public class ResultService {

    private final ResultContext resultContext;
    private final ResultRepository resultRepository;
    //private final AuthorizeService authorizeService;
    private final Validator validator;

    public ResultService(PersistenceContext persistenceContext, AuthorizeService authorizeService,
            Validator validator) {
        this.resultContext = persistenceContext.results;
        this.resultRepository = persistenceContext.results.repository;
        //this.authorizeService = authorizeService;
        this.validator = validator;
    }

    public Result findById(UUID id) {
        return resultContext.repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не вдалось знайти питання"));
    }

    public Set<Result> findAll() {
        return new TreeSet<>(resultRepository.findAll());
    }


    public Set<Result> findAllByTestId(UUID testId) {
        return new TreeSet<>(resultContext.repository.findAllByTestId(testId));
    }

    public Set<Result> findAllByUserId(UUID userId) {
        return new TreeSet<>(resultContext.repository.findAllByOwnerId(userId));
    }

    public long count() {
        return resultRepository.count();
    }

    public Result create(ResultStoreDto resultStoreDto) {
        var violations = validator.validate(resultStoreDto);
        if (!violations.isEmpty()) {
            throw ValidationException.create("збереженні питання", violations);
        }

        Result report = Result.builder()
                .id(null)
                .ownerId(resultStoreDto.ownerId())
                .owner(null)
                .testId(resultStoreDto.testId())
                .test(null)
                .reportId(resultStoreDto.reportId())
                .report(null)
                .mark(resultStoreDto.mark())
                .createdAt(null).build();

        resultContext.registerNew(report);
        resultContext.commit();
        return resultContext.getEntity();
    }

    public boolean delete(UUID id) {
        Result result = findById(id);

        return resultContext.repository.delete(result.getId());
    }
}
