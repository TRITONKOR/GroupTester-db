package com.tritonkor.domain.service.impl;

import com.tritonkor.persistence.context.factory.PersistenceContext;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.entity.User.Role;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public AuthorizeService(PersistenceContext persistenceContext,
        AuthenticationService authenticationService) {
        this.userRepository = persistenceContext.users.repository;
        this.authenticationService = authenticationService;
    }

    public boolean canCreate(UUID userId, DtoTypes dtoTypes) {
        User user = getUser(userId);

        return switch (dtoTypes) {
            case TEST, REPORT -> !user.getRole().equals(Role.ADMIN);
            case USER, RESULT -> true;
        };
    }

    public boolean canUpdate(Test test, UUID userId) {
        User user = getUser(userId);
        return test.getOwnerId() == userId && !user.getRole().equals(Role.STUDENT);
    }

    public boolean canDelete(Test test, UUID userId) {
        User user = getUser(userId);
        return test.getOwnerId() == userId && !user.getRole().equals(Role.STUDENT);
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Користувача не знайдено"));
    }


    public enum DtoTypes {
        TEST,
        REPORT,
        RESULT,
        USER
    }
}
