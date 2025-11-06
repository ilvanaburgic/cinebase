// AuthService.java
package com.sdp.cinebase.auth.service;

import com.sdp.cinebase.auth.dto.AuthResponse;
import com.sdp.cinebase.auth.dto.LoginRequest;
import com.sdp.cinebase.auth.dto.RegisterRequest;
import com.sdp.cinebase.security.JwtService;
import com.sdp.cinebase.user.model.User;
import com.sdp.cinebase.user.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    public AuthResponse register(RegisterRequest req) {
        String name = req.name().trim();
        String surname = req.surname().trim();
        String username = req.username().trim().toLowerCase();
        String email = req.email().trim().toLowerCase();
        String rawPassword = req.password();

        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        User u = new User();
        u.setName(name);
        u.setSurname(surname);
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));

        User saved = userRepository.save(u);

        String token = jwtService.generateToken(saved.getId(), saved.getUsername(), saved.getEmail());

        return new AuthResponse(
                token,
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getName(),
                saved.getSurname()
        );
    }

    public AuthResponse login(LoginRequest req) {
        String id = req.identifier().trim().toLowerCase();
        String raw = req.password();

        var userOpt = id.contains("@")
                ? userRepository.findByEmail(id)
                : userRepository.findByUsername(id);

        var user = userOpt.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(raw, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getEmail());
        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getSurname()
        );
    }
}
