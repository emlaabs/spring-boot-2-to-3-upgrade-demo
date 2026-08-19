package org.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
public class VerificationToken {

    @Id
    private String token;

    private String username;
    private Instant expiresAt;

    public VerificationToken() {}

    public VerificationToken(String token, String username, Instant expiresAt) {
        this.token = token;
        this.username = username;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
