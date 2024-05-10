package com.tritonkor.domain.service.impl;

import com.tritonkor.domain.dto.QuestionStoreDto;
import com.tritonkor.domain.dto.QuestionUpdateDto;
import com.tritonkor.domain.exception.ValidationException;
import com.tritonkor.persistence.context.factory.PersistenceContext;
import com.tritonkor.persistence.context.impl.QuestionContext;
import com.tritonkor.persistence.entity.Question;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.QuestionRepository;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    private final QuestionContext questionContext;
    private final QuestionRepository questionRepository;
    //private final AuthorizeService authorizeService;
    private final Validator validator;

    public QuestionService(PersistenceContext persistenceContext, AuthorizeService authorizeService,
            Validator validator) {
        this.questionContext = persistenceContext.questions;
        this.questionRepository = persistenceContext.questions.repository;
       // this.authorizeService = authorizeService;
        this.validator = validator;
    }

    public Question findById(UUID id) {
        return questionContext.repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не вдалось знайти питання"));
    }

    public Set<Question> findAll() {
        return new TreeSet<>(questionRepository.findAll());
    }


    public Set<Question> findAllByTestId(UUID testId) {
        return new TreeSet<>(questionContext.repository.findAllByTestId(testId));
    }

    public long count() {
        return questionRepository.count();
    }

    public Question create(QuestionStoreDto questionStoreDto) {
        var violations = validator.validate(questionStoreDto);
        if (!violations.isEmpty()) {
            throw ValidationException.create("збереженні питання", violations);
        }

        Question question = Question.builder()
                .id(null)
                .text(questionStoreDto.text())
                .answers(null)
                .testId(questionStoreDto.testId())
                .test(null)
                .build();

        questionContext.registerNew(question);
        questionContext.commit();
        return questionContext.getEntity();
    }

    public Question update(QuestionUpdateDto questionUpdateDto) {
        var violations = validator.validate(questionUpdateDto);
        if (!violations.isEmpty()) {
            throw ValidationException.create("оновленні питання", violations);
        }


        Question question = Question.builder()
                .id(questionUpdateDto.id())
                .text(questionUpdateDto.text())
                .answers(null)
                .testId(questionUpdateDto.testId())
                .test(null)
                .build();

        questionContext.registerModified(question);
        questionContext.commit();
        return questionContext.getEntity();
    }

    public boolean delete(UUID id) {
        Question question = findById(id);

        return questionContext.repository.delete(question.getId());
    }
}
