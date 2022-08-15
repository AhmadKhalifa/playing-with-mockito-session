package com.mockito.playground.user.validator;

public interface Validator<T> {

    boolean isValid(T value);
}
