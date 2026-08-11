package org.example.demo.integration;

import org.example.demo.security.JwtUtil;
import org.example.demo.IntegrationTestSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.config.name=application-test")
@AutoConfigureMockMvc
@Import(IntegrationTestSetup.class)
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void securedEndpointShouldWorkWithRealJwt() throws Exception {
        // Generate a real JWT using your actual JwtUtil
        String token = jwtUtil.generateToken("eric");

        mockMvc.perform(get("/books")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void securedEndpointShouldRejectMissingToken() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void securedEndpointShouldRejectInvalidToken() throws Exception {
        mockMvc.perform(get("/books")
                        .header("Authorization", "Bearer not-a-real-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminCanDeleteBook() throws Exception {
        String token = jwtUtil.generateToken("admin");

        mockMvc.perform(delete("/books/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void userCannotDeleteBook() throws Exception {
        String token = jwtUtil.generateToken("eric"); // ROLE_USER

        mockMvc.perform(delete("/books/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBookWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isUnauthorized());
    }

}
