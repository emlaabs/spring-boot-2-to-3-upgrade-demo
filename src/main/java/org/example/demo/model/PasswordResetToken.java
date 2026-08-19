package org.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


import java.time.Instant;

@Entity
public class PasswordResetToken {

    @Id
    private String token;

    private String username;

    private Instant expiresAt;

    public PasswordResetToken() {}

    public PasswordResetToken(String token, String username, Instant expiresAt) {
        this.token = token;
        this.username = username;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }

    public String getUsername() {
        return username;
    }
}

