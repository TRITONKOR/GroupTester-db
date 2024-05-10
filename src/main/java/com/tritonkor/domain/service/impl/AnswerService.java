package com.tritonkor.domain.service.impl;

import com.tritonkor.domain.dto.AnswerStorDto;
import com.tritonkor.domain.dto.AnswerUpdateDto;
import com.tritonkor.domain.exception.ValidationException;
import com.tritonkor.persistence.context.factory.PersistenceContext;
import com.tritonkor.persistence.context.impl.AnswerContext;
import com.tritonkor.persistence.entity.Answer;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.AnswerRepository;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {

    private final AnswerContext answerContext;
    private final AnswerRepository answerRepository;
    //private final AuthorizeService authorizeService;
    private final Validator validator;

    public AnswerService(PersistenceContext persistenceContext, AuthorizeService authorizeService,
            Validator validator) {
        this.answerContext = persistenceContext.answers;
        this.answerRepository = persistenceContext.answers.repository;
        // this.authorizeService = authorizeService;
        this.validator = validator;
    }

    public Answer findById(UUID id) {
        return answerContext.repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не вдалось знайти відповідь"));
    }

    public Set<Answer> findAll() {
        return new TreeSet<>(answerRepository.findAll());
    }


    public Set<Answer> findAllByTestId(UUID questionId) {
        return new TreeSet<>(answerContext.repository.findAllByQuestionId(questionId));
    }

    public long count() {
        return answerRepository.count();
    }

    public Answer create(AnswerStorDto answerStorDto) {
        var violations = validator.validate(answerStorDto);
        if (!violations.isEmpty()) {
            throw ValidationException.create("збереженні відповіді", violations);
        }

        Answer answer = Answer.builder()
                .id(null)
                .questionId(answerStorDto.questionId())
                .question(null)
                .text(answerStorDto.text())
                .isCorrect(answerStorDto.isCorrect())
                .build();

        answerContext.registerNew(answer);
        answerContext.commit();
        return answerContext.getEntity();
    }

    public Answer update(AnswerUpdateDto answerUpdateDto) {
        var violations = validator.validate(answerUpdateDto);
        if (!violations.isEmpty()) {
            throw ValidationException.create("оновленні відповіді", violations);
        }

        Answer answer = Answer.builder()
                .id(answerUpdateDto.id())
                .questionId(answerUpdateDto.questionId())
                .question(null).text(answerUpdateDto.text())
                .isCorrect(answerUpdateDto.isCorrect())
                .build();

        answerContext.registerModified(answer);
        answerContext.commit();
        return answerContext.getEntity();
    }

    public boolean delete(UUID id) {
        Answer answer = findById(id);

        return answerContext.repository.delete(answer.getId());
    }
}
