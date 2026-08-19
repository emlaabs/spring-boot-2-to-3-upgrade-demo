package org.example.demo.security;

import org.example.demo.model.Role;
import org.example.demo.model.User;
import org.example.demo.repository.UserRepository;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Phase 14: enforce lockout BEFORE authentication
        if (user.isLocked()) {
            throw new LockedException("Account locked until " + user.getLockoutUntil());
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream().map(Role::getName).toArray(String[]::new))
                .build();
    }
}
