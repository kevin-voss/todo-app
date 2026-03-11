package com.todo.service;

import com.todo.entity.User;
import com.todo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Optional<String> signup(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            return Optional.empty();
        }
        String trimmedEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmail(trimmedEmail)) {
            return Optional.empty();
        }
        String hash = passwordEncoder.encode(password);
        User user = new User(trimmedEmail, hash);
        userRepository.save(user);
        return Optional.of(jwtService.createToken(user.getId(), trimmedEmail));
    }

    public Optional<String> login(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email.trim().toLowerCase())
                .filter(user -> passwordEncoder.matches(password, user.getPasswordHash()))
                .map(user -> jwtService.createToken(user.getId(), user.getEmail()));
    }
}
