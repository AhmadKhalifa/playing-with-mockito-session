package com.mockito.playground.user.exception;

public class InvalidVipUsernameException extends RuntimeException {

    public InvalidVipUsernameException(String username) {
        super(String.format("Invalid VIP username %s", username));
    }
}
