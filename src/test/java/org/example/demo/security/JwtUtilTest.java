package org.example.demo.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil = new JwtUtil();

    @Test
    void generateTokenAndValidate() {
        String token = jwtUtil.generateToken("eric");

        assertTrue(jwtUtil.validate(token));
        assertEquals("eric", jwtUtil.extractUsername(token));
    }

    @Test
    void validateShouldFailForInvalidToken() {
        assertFalse(jwtUtil.validate("not-a-token"));
    }
}
