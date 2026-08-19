package org.example.demo.controller;

import org.example.demo.model.User;
import org.example.demo.repository.UserRepository;
import org.example.demo.security.CustomUserDetailsService;
import org.example.demo.security.JwtAuthFilter;
import org.example.demo.security.JwtUtil;
import org.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private UserRepository userRepository;

    @Test
    void loginShouldReturnToken() throws Exception {

        // Mock authentication
        Mockito.when(authenticationManager.authenticate(
                Mockito.any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(new UsernamePasswordAuthenticationToken("eric", "password"));

        // Mock user lookup
        Mockito.when(userService.findByUsername("eric"))
                .thenReturn(new User("eric", "encoded", "eric@test.com", true));

        // Mock token generation
        Mockito.when(jwtUtil.generateToken("eric")).thenReturn("fake-jwt");
        Mockito.when(jwtUtil.generateRefreshToken("eric")).thenReturn("fake-refresh");

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                        {"username":"eric","password":"password"}
                        """))
                .andExpect(status().isOk());
    }
}
