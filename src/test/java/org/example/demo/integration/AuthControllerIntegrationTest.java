package org.example.demo.integration;

import com.jayway.jsonpath.JsonPath;
import org.antlr.v4.runtime.misc.LogManager;
import org.example.demo.IntegrationTestSetup;
import org.example.demo.TestDataSeeder;
import org.example.demo.model.PasswordResetToken;
import org.example.demo.model.User;
import org.example.demo.model.VerificationToken;
import org.example.demo.repository.PasswordResetTokenRepository;
import org.example.demo.repository.RoleRepository;
import org.example.demo.repository.UserRepository;
import org.example.demo.repository.VerificationTokenRepository;
import org.example.demo.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(IntegrationTestSetup.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private VerificationTokenRepository tokenRepo;

    @Autowired
    private UserRepository userRepository;   // REQUIRED

    @Autowired
    private PasswordEncoder passwordEncoder; // REQUIRED

    @Autowired
    private PasswordResetTokenRepository resetTokenRepository;

    @Autowired
    private RoleRepository roleRepository;



    @BeforeEach
    void cleanDatabase() {
        resetTokenRepository.deleteAll();
        tokenRepo.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        TestDataSeeder.seed(roleRepository, userRepository, passwordEncoder);
    }


    @Test
    void loginShouldReturnJwtToken() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                        {
                          "username": "eric",
                          "password": "password"
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

    @Test
    void userCanRegisterAndReceiveVerificationToken() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"username":"newuser","password":"password123","email":"new@user.com"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationToken").exists());
    }

    @Test
    void userCanVerifyEmail() throws Exception {
        // First register
        var result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"username":"verifyme","password":"password123","email":"v@me.com"}
                        """))
                .andReturn();

        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.verificationToken");

        mockMvc.perform(get("/auth/verify?token=" + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("verified"));
    }

    @Test
    void invalidTokenShouldReturnInvalidStatus() throws Exception {
        mockMvc.perform(get("/auth/verify?token=not-real"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("invalid"));
    }

    @Test
    void unverifiedUserCannotLogin() throws Exception {
        // Create unverified user
                userRepository.save(new User("newbie",
                passwordEncoder.encode("password"),
                "newbie@test.com",
                false));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"username":"newbie","password":"password"}
                    """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expiredVerificationTokenShouldReturnInvalid() throws Exception {
        VerificationToken token = new VerificationToken(
                "expired-token",
                "eric",
                Instant.now().minusSeconds(3600)
        );
        tokenRepo.save(token);

        mockMvc.perform(get("/auth/verify?token=expired-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Verification token expired"));
    }

    @Test
    void userCanRequestPasswordReset() throws Exception {

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"email":"eric@test.com"}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resetToken").exists());
    }

    @Test
    void userCanResetPassword() throws Exception {

        // DO NOT create Eric again — he already exists from the seeder

        String token = UUID.randomUUID().toString();
        resetTokenRepository.save(new PasswordResetToken(
                token, "eric", Instant.now().plusSeconds(3600)
        ));

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {"token":"%s","newPassword":"newpassword123"}
                """.formatted(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("password_reset"));
    }

    @Test
    void expiredResetTokenShouldFail() throws Exception {

        resetTokenRepository.save(new PasswordResetToken(
                "expired", "eric", Instant.now().minusSeconds(3600)
        ));

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"token":"expired","newPassword":"newpassword123"}
                    """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Reset token expired"));
    }

    @Test
    void accountLocksAfterFiveFailedAttempts() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/auth/login")
                            .contentType("application/json")
                            .content("""
                    {"username":"eric","password":"wrong"}
                """))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                {"username":"eric","password":"password"}
            """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("ACCOUNT_LOCKED"));
    }

    @Test
    void accountUnlocksAfterCooldown() throws Exception {
        User eric = userRepository.findByUsername("eric").get();
        eric.lockFor(Duration.ofSeconds(1));
        userRepository.save(eric);

        Thread.sleep(1500);

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                {"username":"eric","password":"password"}
            """))
                .andExpect(status().isOk());
    }

    @Test
    void successfulLoginResetsFailedAttempts() throws Exception {
        User eric = userRepository.findByUsername("eric").get();
        eric.incrementFailedAttempts();
        eric.incrementFailedAttempts();
        userRepository.save(eric);

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                {"username":"eric","password":"password"}
            """))
                .andExpect(status().isOk());

        User updated = userRepository.findByUsername("eric").get();
        assertEquals(0, updated.getFailedLoginAttempts());

    }

}
