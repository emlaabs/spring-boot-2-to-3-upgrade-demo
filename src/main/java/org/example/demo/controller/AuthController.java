package org.example.demo.controller;

import jakarta.validation.Valid;
import org.example.demo.dto.ForgotPasswordRequest;
import org.example.demo.dto.RefreshRequest;
import org.example.demo.dto.RegistrationRequest;
import org.example.demo.dto.ResetPasswordRequest;
import org.example.demo.model.User;
import org.example.demo.repository.UserRepository;
import org.example.demo.security.JwtUtil;
import org.example.demo.security.CustomUserDetailsService;
import org.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

record LoginRequest(String username, String password) {}
record LoginResponse(String token, String refreshToken) {}

// Logout is handled client-side by deleting access and refresh tokens.
// JWT is stateless; server does not track sessions or tokens.

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    private final UserRepository userRepository;

    public AuthController(
            AuthenticationManager authManager,
            CustomUserDetailsService userDetailsService,
            JwtUtil jwtUtil,
            UserService userService,
            UserRepository userRepository
    ) {
        this.authManager = authManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        User user = userRepository.findByUsername(request.username()).orElse(null);

        // Phase 12 rule: unverified users cannot log in
        if (user != null && !user.isVerified()) {
            throw new BadCredentialsException("User is not verified");
        }

        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(), request.password()
                    )
            );

            // SUCCESS → reset attempts
            if (user != null) {
                user.resetFailedAttempts();
                userRepository.save(user);
            }

            return new LoginResponse(
                    jwtUtil.generateToken(request.username()),
                    jwtUtil.generateRefreshToken(request.username())
            );

        } catch (LockedException ex) {
            if (user != null) {
                throw new LockedException("Account locked until " + user.getLockoutUntil());
            }
            throw new LockedException("Account is locked");
        } catch (BadCredentialsException ex) {

            if (user != null) {
                user.incrementFailedAttempts();

                if (user.getFailedLoginAttempts() >= 5) {
                    user.lockFor(Duration.ofMinutes(15));
                }

                userRepository.save(user);
            }

            throw ex; // important: do NOT wrap again
        }
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody RefreshRequest request) {

        String refreshToken = request.refreshToken();

        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new AuthenticationException("Invalid refresh token") {};
        }

        String username = jwtUtil.extractUsername(refreshToken);
        String newAccessToken = jwtUtil.generateToken(username);

        return Map.of("token", newAccessToken);
    }

    @PostMapping("/register")
    public Map<String, String> register(@Valid @RequestBody RegistrationRequest request) {
        String token = userService.register(request);
        return Map.of("verificationToken", token);
    }

    @GetMapping("/verify")
    public Map<String, String> verify(@RequestParam String token) {
        boolean success = userService.verify(token);
        return Map.of("status", success ? "verified" : "invalid");
    }

    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String token = userService.forgotPassword(request.email());
        return Map.of("resetToken", token);
    }

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.token(), request.newPassword());
        return Map.of("status", "password_reset");
    }

}
