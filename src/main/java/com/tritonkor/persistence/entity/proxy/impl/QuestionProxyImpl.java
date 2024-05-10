package com.tritonkor.persistence.entity.proxy.impl;

import com.tritonkor.persistence.entity.Question;
import com.tritonkor.persistence.entity.proxy.contract.QuestionProxy;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.QuestionRepository;
import java.util.UUID;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class QuestionProxyImpl implements QuestionProxy {
    private final ApplicationContext applicationContext;

    public QuestionProxyImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Question get(UUID entityId) {
        QuestionProxy proxy = (questionId) -> applicationContext.getBean(QuestionRepository.class)
                .findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Не вдалось знайти питання за id"));

        return proxy.get(entityId);
    }
}
