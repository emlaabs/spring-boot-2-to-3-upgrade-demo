package org.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column(nullable = false, unique = true)
    private String username;

    @Setter
    @Getter
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Column(nullable = false)
    private boolean verified = false;

    @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Getter
    private int failedLoginAttempts;
    private Instant lockoutUntil;


    public User() {}

    public User(String username, String password, String email, boolean verified) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.verified = verified;
    }

    public String getEmail() { return email; }
    public boolean isVerified() { return verified; }
    public Set<Role> getRoles() { return roles; }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLocked() {
        return lockoutUntil != null && lockoutUntil.isAfter(Instant.now());
    }

    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
    }

    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.lockoutUntil = null;
    }

    public void lockFor(Duration duration) {
        this.lockoutUntil = Instant.now().plus(duration);
    }

    public Instant getLockoutUntil() {
        return lockoutUntil;
    }

}
