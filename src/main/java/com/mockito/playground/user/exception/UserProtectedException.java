package com.mockito.playground.user.exception;

public class UserProtectedException extends RuntimeException {

    public UserProtectedException(long userId) {
        super(String.format("You're not authorized to get user %d details", userId));
    }
}
