package org.example.demo.integration;

import org.example.demo.IntegrationTestSetup;
import org.example.demo.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {IntegrationTestSetup.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void loginShouldReturnJwtToken() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                        {
                          "username": "eric",
                          "password": "password123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void loginShouldFailWithBadCredentials() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                        {
                          "username": "eric",
                          "password": "wrong"
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshTokenShouldReturnNewAccessToken() throws Exception {
        String refreshToken = jwtUtil.generateRefreshToken("eric");

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void invalidRefreshTokenShouldReturn401() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"not-a-real-token\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expiredRefreshTokenShouldReturn401() throws Exception {
        String expiredToken = jwtUtil.generateExpiredRefreshToken("eric");

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + expiredToken + "\"}"))
                .andExpect(status().isUnauthorized());
    }
}
