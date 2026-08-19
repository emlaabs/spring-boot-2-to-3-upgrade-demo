package org.example.demo.service;

import org.example.demo.dto.RegistrationRequest;
import org.example.demo.model.PasswordResetToken;
import org.example.demo.model.User;
import org.example.demo.model.VerificationToken;
import org.example.demo.repository.PasswordResetTokenRepository;
import org.example.demo.repository.UserRepository;
import org.example.demo.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository resetTokenRepository;

    public UserService(UserRepository userRepository,
                       VerificationTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder, PasswordResetTokenRepository resetTokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.resetTokenRepository = resetTokenRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

        public String register(RegistrationRequest request) {
            if (userRepository.existsByUsername(request.username())) {
                throw new IllegalArgumentException("Username already exists");
            }

            String email = request.email().trim().toLowerCase();

            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email already exists");
            }

            User user = new User(
                    request.username(),
                    passwordEncoder.encode(request.password()),
                    email,
                    false
            );

            userRepository.save(user);

            String token = UUID.randomUUID().toString();
            Instant expiresAt = Instant.now().plusSeconds(3600);

            tokenRepository.save(new VerificationToken(token, user.getUsername(), expiresAt));

            return token;
    }

    public boolean verify(String token) {
        return tokenRepository.findById(token)
                .map(t -> {
                    if (t.getExpiresAt().isBefore(Instant.now())) {
                        tokenRepository.deleteById(token);
                        throw new IllegalStateException("Verification token expired");
                    }

                    User user = userRepository.findByUsername(t.getUsername()).orElseThrow();
                    user.setVerified(true);
                    userRepository.save(user);
                    tokenRepository.deleteById(token);
                    return true;
                })
                .orElse(false);
    }

    public String forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(3600);

        resetTokenRepository.save(new PasswordResetToken(token, user.getUsername(), expiresAt));

        return token; // In real life, you'd email this
    }

    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = resetTokenRepository.findById(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (resetToken.isExpired()) {
            resetTokenRepository.deleteById(token);
            throw new IllegalStateException("Reset token expired");
        }

        User user = userRepository.findByUsername(resetToken.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetTokenRepository.deleteById(token);
    }

}
