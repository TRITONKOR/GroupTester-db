package com.tritonkor.domain.exception;

public class UserAlreadyAuthenticatedException extends RuntimeException {

    public UserAlreadyAuthenticatedException(String message) {
        super(message);
    }
}

