package com.mockito.playground.user.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {

    private final long id;
    private final String username;
    private final boolean vip;
    private LocalDateTime createdAt;

    public User(long id, String username, boolean vip) {
        this.id = id;
        this.username = username;
        this.vip = vip;
        this.createdAt = null;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isVip() {
        return vip;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void markAsCreated() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        User user = (User) other;
        return id == user.id &&
                vip == user.vip &&
                Objects.equals(username, user.username) &&
                Objects.equals(createdAt, user.createdAt);
    }
}
