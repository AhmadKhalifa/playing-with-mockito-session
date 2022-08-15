package com.mockito.playground.user.repository;

import com.mockito.playground.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(long id);

    boolean userExists(String username);

    User save(User user);

    long count();
}
