package com.tritonkor.persistence.entity.proxy.impl;

import com.tritonkor.persistence.entity.Question;
import com.tritonkor.persistence.entity.proxy.contract.Questions;
import com.tritonkor.persistence.repository.contract.QuestionRepository;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class QuestionsProxy implements Questions {
    private final ApplicationContext applicationContext;

    public QuestionsProxy(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Set<Question> get(UUID testId) {
        QuestionRepository testRepository = applicationContext.getBean(QuestionRepository.class);
        Questions questions = tId -> Collections.unmodifiableSet(testRepository.findAllByTestId(tId));
        return questions.get(testId);
    }
}
