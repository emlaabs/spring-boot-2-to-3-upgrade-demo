package org.example.demo.security;

import org.example.demo.model.Role;
import org.example.demo.model.User;
import org.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsername() {
        Role role = new Role();
        role.setName("ROLE_USER");

        User user = new User();
        user.setUsername("eric");
        user.setPassword("pass");
        user.setRoles(Set.of(role));

        when(repo.findByUsername("eric")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("eric");

        assertEquals("eric", details.getUsername());
    }

    @Test
    void loadUserByUsernameNotFound() {
        when(repo.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing"));
    }
}
