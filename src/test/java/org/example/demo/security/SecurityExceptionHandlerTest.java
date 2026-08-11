package org.example.demo.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SecurityExceptionHandlerTest {

    private final SecurityExceptionHandler handler = new SecurityExceptionHandler();

    @Test
    void handleBadCredentials() {
        Map<String, String> response = handler.handleBadCredentials(
                new BadCredentialsException("Bad credentials")
        );

        assertEquals("UNAUTHORIZED", response.get("error"));
        assertEquals("Invalid username or password", response.get("message"));
    }

    @Test
    void handleAuthenticationException() {
        Map<String, String> response = handler.handleAuthentication(
                new AuthenticationException("Auth failed") {}
        );

        assertEquals("UNAUTHORIZED", response.get("error"));
        assertEquals("Authentication failed", response.get("message"));
    }

    @Test
    void handleAccessDenied() {
        Map<String, String> response = handler.handleAccessDenied(
                new AccessDeniedException("Forbidden")
        );

        assertEquals("FORBIDDEN", response.get("error"));
        assertEquals("You do not have permission to access this resource", response.get("message"));
    }

    @Test
    void handleGenericException() {
        Map<String, String> response = handler.handleGeneric(
                new RuntimeException("Unexpected")
        );

        assertEquals("INTERNAL_SERVER_ERROR", response.get("error"));
        assertEquals("An unexpected error occurred", response.get("message"));
    }
}
