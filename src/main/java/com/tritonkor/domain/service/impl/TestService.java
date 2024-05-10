package com.tritonkor.domain.service.impl;


import com.tritonkor.domain.dto.TestStoreDto;
import com.tritonkor.domain.dto.TestUpdateDto;
import com.tritonkor.domain.exception.AccessDeniedException;
import com.tritonkor.domain.exception.ValidationException;
import com.tritonkor.domain.service.impl.AuthorizeService.DtoTypes;
import com.tritonkor.persistence.context.factory.PersistenceContext;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.filter.TestFilterDto;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.context.impl.TestContext;
import com.tritonkor.persistence.repository.contract.TestRepository;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    private final TestContext testContext;
    private final TestRepository testRepository;
    private final AuthorizeService authorizeService;
    private final Validator validator;

    public TestService(PersistenceContext persistenceContext, AuthorizeService authorizeService,
            Validator validator) {
        this.testContext = persistenceContext.tests;
        this.testRepository = persistenceContext.tests.repository;
        this.authorizeService = authorizeService;
        this.validator = validator;
    }

    public Test findById(UUID id) {
        return testContext.repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не вдалось знайти тест"));
    }

    public Set<Test> findAll() {
        return new TreeSet<>(testRepository.findAll());
    }

    public Set<Test> findAll(int offset,
            int limit,
            String sortColumn,
            boolean ascending,
            TestFilterDto testFilterDto) {
        return new TreeSet<>(testRepository.findAll(
                offset,
                limit,
                sortColumn,
                ascending,
                testFilterDto));
    }

    public Set<Test> findAllByUserId(UUID userId) {
        return new TreeSet<>(testRepository.findAllByUserId(userId));
    }

    public Set<Test> findAllByUserId(UUID userId,
            int offset,
            int limit,
            String sortColumn,
            boolean ascending,
            TestFilterDto testFilterDto) {
        return new TreeSet<>(testRepository.findAllByUserId(
                userId,
                offset,
                limit,
                sortColumn,
                ascending,
                testFilterDto));
    }

    public long count() {
        return testRepository.count();
    }

    public Test create(TestStoreDto testStoreDto) {
        var violations = validator.validate(testStoreDto);
        if (!violations.isEmpty()) {
            throw ValidationException.create("збереженні тесту", violations);
        } else if (!authorizeService.canCreate(testStoreDto.ownerId(), DtoTypes.TEST)) {
            throw AccessDeniedException.notTeacherUser("створювати тести");
        }

        Test comment = Test.builder()
                .id(null)
                .title(testStoreDto.title())
                .ownerId(testStoreDto.ownerId())
                .owner(null)
                .questions(null)
                .tags(null)
                .createdAt(null)
                .build();

        testContext.registerNew(comment);
        testContext.commit();
        return testContext.getEntity();
    }

    public Test update(TestUpdateDto testUpdateDto) {
        var violations = validator.validate(testUpdateDto);
        if (!violations.isEmpty()) {
            throw ValidationException.create("оновленні тесту", violations);
        }

        Test oldTest = findById(testUpdateDto.id());

        if (!authorizeService.canUpdate(oldTest, testUpdateDto.ownerId())) {
            throw AccessDeniedException.notAuthorUser("оновлювати тести");
        }

        Test comment = Test.builder()
                .id(testUpdateDto.id())
                .title(testUpdateDto.title())
                .ownerId(testUpdateDto.ownerId())
                .owner(null)
                .questions(null)
                .tags(null)
                .createdAt(null)
                .build();

        testContext.registerModified(comment);
        testContext.commit();
        return testContext.getEntity();
    }

    public boolean delete(UUID id, UUID userId) {
        Test test = findById(id);
        if (!authorizeService.canDelete(test, userId)) {
            throw AccessDeniedException.notAuthorUser("видаляти тести");
        }

        return testContext.repository.delete(test.getId());
    }
}
