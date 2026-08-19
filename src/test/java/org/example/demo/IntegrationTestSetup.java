package org.example.demo;

import org.example.demo.model.Role;
import org.example.demo.model.User;
import org.example.demo.repository.RoleRepository;
import org.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@TestConfiguration
public class IntegrationTestSetup {

    @Bean
    public CommandLineRunner testDataLoader(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        return args -> TestDataSeeder.seed(roleRepository, userRepository, passwordEncoder);
    }
}



