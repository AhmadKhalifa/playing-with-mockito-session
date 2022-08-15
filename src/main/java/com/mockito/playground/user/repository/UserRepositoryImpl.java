package com.mockito.playground.user.repository;

import com.mockito.playground.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private final List<User> users = new ArrayList<>(); // Imaginary database DAO for simplicity

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public Optional<User> findById(long id) {
        return users
                .stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    @Override
    public boolean userExists(String username) {
        return users
                .stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    @Override
    public User save(User user) {
        user.markAsCreated();
        users.add(user);
        return user;
    }

    @Override
    public long count() {
        return users.size();
    }
}
