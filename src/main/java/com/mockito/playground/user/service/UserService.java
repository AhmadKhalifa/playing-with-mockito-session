package com.mockito.playground.user.service;

import com.mockito.playground.user.exception.InvalidVipUsernameException;
import com.mockito.playground.user.exception.UserNotFoundException;
import com.mockito.playground.user.exception.UserProtectedException;
import com.mockito.playground.user.exception.UsernameAlreadyExistsException;
import com.mockito.playground.user.model.User;
import com.mockito.playground.user.repository.UserRepository;
import com.mockito.playground.user.validator.UniqueUsernameValidator;
import com.mockito.playground.user.validator.VipUsernameValidator;

import java.util.List;
import java.util.stream.Collectors;

public class UserService {

    private final UserRepository userRepository;
    private final UniqueUsernameValidator uniqueUsernameValidator;
    private final VipUsernameValidator vipUsernameValidator;

    public UserService(
            UserRepository userRepository,
            UniqueUsernameValidator uniqueUsernameValidator,
            VipUsernameValidator vipUsernameValidator
    ) {
        this.userRepository = userRepository;
        this.uniqueUsernameValidator = uniqueUsernameValidator;
        this.vipUsernameValidator = vipUsernameValidator;
    }

    /**
     * 1. Empty list of users
     * 2. Mix of normal and VIP users => should return only normal users ->
     * 3. Error handling for DB errors
     * 4. All normal users
     */
    public List<User> getAll() {
        return userRepository
                .findAll()
                .stream()
                .filter(user -> !user.isVip())
                .collect(Collectors.toList());
    }

    /**
     * 1. Returns a 'Normal' user if exists
     * 2. Throws an exception if a VIP user is queried
     * 3. Throws an exception if user doesn't exist
     */
    public User getUser(long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException(userId);
                });
        if (user.isVip()) {
            throw new UserProtectedException(userId);
        }
        return user;
    }

    /**
     * 1. Register with a normal user and unique username
     * 2. Register with a VIP with unique username and valid vip username
     * 3. Throws an exception when user already exists
     * 4. Register with normal user with invalid (Hussein)
     * 5. Registers with a non-valid vip username and unique username
     */
    public User register(String username, boolean isVip) {
        if (!uniqueUsernameValidator.isValid(username)) {
            throw new UsernameAlreadyExistsException(username);
        }
        if (isVip && !vipUsernameValidator.isValid(username)) {
            throw new InvalidVipUsernameException(username);
        }
        long userId = userRepository.count();
        User user = new User(userId, username, isVip);
        return userRepository.save(user);
    }
}
