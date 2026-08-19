package org.example.demo;

import org.example.demo.model.Role;
import org.example.demo.model.User;
import org.example.demo.repository.RoleRepository;
import org.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestDataSeeder {

    public static void seed(RoleRepository roleRepository,
                            UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {

        Role adminRole = roleRepository.save(new Role("ROLE_ADMIN"));
        Role userRole = roleRepository.save(new Role("ROLE_USER"));

        User admin = new User(
                "admin",
                passwordEncoder.encode("password"),
                "admin@test.com",
                true
        );
        admin.getRoles().add(adminRole);
        userRepository.save(admin);

        User eric = new User(
                "eric",
                passwordEncoder.encode("password"),
                "eric@test.com",
                true
        );
        eric.getRoles().add(userRole);
        userRepository.save(eric);
    }
}
