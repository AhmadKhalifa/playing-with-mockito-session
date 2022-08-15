package com.mockito.playground.user.validator;

import com.mockito.playground.user.repository.UserRepository;

public class UniqueUsernameValidator implements Validator<String> {

    private final UserRepository userRepository;

    public UniqueUsernameValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String value) {
        return !userRepository.userExists(value);
    }
}
