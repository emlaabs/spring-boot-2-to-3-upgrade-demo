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
            UserRepository userRepo,
            RoleRepository roleRepo,
            PasswordEncoder encoder) {

        return args -> {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepo.save(adminRole);

            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepo.save(userRole);

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("password123"));
            admin.setRoles(Set.of(adminRole));
            userRepo.save(admin);

            User user = new User();
            user.setUsername("eric");
            user.setPassword(encoder.encode("password123"));
            user.setRoles(Set.of(userRole));
            userRepo.save(user);
        };
    }
}


