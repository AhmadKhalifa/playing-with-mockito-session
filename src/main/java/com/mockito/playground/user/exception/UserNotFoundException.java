package com.mockito.playground.user.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(long userId) {
        super(String.format("User %d not found", userId));
    }
}
