package com.tritonkor.domain.service.impl;

import com.tritonkor.domain.exception.AuthenticationException;
import com.tritonkor.domain.exception.UserAlreadyAuthenticatedException;
import com.tritonkor.persistence.context.factory.PersistenceContext;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.password4j.Password;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private User user;

    AuthenticationService(PersistenceContext persistenceContext) {
        this.userRepository = persistenceContext.users.repository;
    }

    public boolean authenticate(String username, String password) {
        // Перевіряємо, чи вже існує аутентифікований користувач
        if (user != null) {
            throw new UserAlreadyAuthenticatedException(
                    STR."Ви вже авторизувалися як: \{user.getUsername()}");
        }

        User foundedUser = userRepository.findByUsername(username)
                .orElseThrow(AuthenticationException::new);

        if (!Password.check(password, foundedUser.getPassword()).withBcrypt()) {
            return false;
        }

        user = foundedUser;
        return true;
    }

    public boolean isAuthenticated() {
        return user != null;
    }

    public User getUser() {
        return user;
    }

    public void logout() {
        if (user == null) {
            throw new UserAlreadyAuthenticatedException("Ви ще не автентифікавані.");
        }
        user = null;
    }

}
