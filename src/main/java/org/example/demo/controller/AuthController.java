package org.example.demo.controller;

import org.example.demo.dto.RefreshRequest;
import org.example.demo.security.JwtUtil;
import org.example.demo.security.CustomUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

record LoginRequest(String username, String password) {}
record LoginResponse(String token) {}

// Logout is handled client-side by deleting access and refresh tokens.
// JWT is stateless; server does not track sessions or tokens.

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(
            AuthenticationManager authManager,
            CustomUserDetailsService userDetailsService,
            JwtUtil jwtUtil
    ) {
        this.authManager = authManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails user = userDetailsService.loadUserByUsername(request.username());
        String token = jwtUtil.generateToken(user.getUsername());

        return new LoginResponse(token);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleBadCredentials(BadCredentialsException ex) {
        return Map.of(
                "error", "UNAUTHORIZED",
                "message", "Invalid username or password"
        );
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
}
