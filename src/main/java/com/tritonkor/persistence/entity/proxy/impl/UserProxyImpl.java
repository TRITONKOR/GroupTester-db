package com.tritonkor.persistence.entity.proxy.impl;

import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.entity.proxy.contract.UserProxy;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.UserRepository;
import java.util.UUID;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class UserProxyImpl implements UserProxy {
    private final ApplicationContext applicationContext;

    public UserProxyImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public User get(UUID entityId) {
        UserProxy proxy = (userId) -> applicationContext.getBean(UserRepository.class)
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Не вдалось знайти користувача за id"));

        return proxy.get(entityId);
    }
}
