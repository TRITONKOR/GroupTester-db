package com.tritonkor.domain.service.impl;

import com.tritonkor.domain.exception.AuthenticationException;
import com.tritonkor.domain.exception.UserAlreadyAuthenticatedException;
import com.tritonkor.persistence.context.factory.PersistenceContext;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.password4j.Password;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for user authentication.
 */
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private User user;

    AuthenticationService(PersistenceContext persistenceContext) {
        this.userRepository = persistenceContext.users.repository;
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return true if authentication is successful, false otherwise
     * @throws UserAlreadyAuthenticatedException if a user is already authenticated
     * @throws AuthenticationException          if authentication fails
     */
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

    /**
     * Checks if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return user != null;
    }

    /**
     * Retrieves the authenticated user.
     *
     * @return the authenticated user
     */
    public User getUser() {
        return user;
    }

    /**
     * Logs out the authenticated user.
     *
     * @throws UserAlreadyAuthenticatedException if no user is currently authenticated
     */
    public void logout() {
        if (user == null) {
            throw new UserAlreadyAuthenticatedException("Ви ще не автентифікавані.");
        }
        user = null;
    }

}
