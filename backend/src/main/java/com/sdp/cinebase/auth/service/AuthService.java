package com.sdp.cinebase.auth.service;

import com.sdp.cinebase.auth.dto.AuthResponse;
import com.sdp.cinebase.auth.dto.LoginRequest;
import com.sdp.cinebase.auth.dto.RegisterRequest;
import com.sdp.cinebase.security.JwtService;
import com.sdp.cinebase.user.model.User;
import com.sdp.cinebase.user.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

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

        log.info("Registration attempt for username: {}, email: {}", username, email);

        if (userRepository.existsByUsername(username)) {
            log.warn("Registration failed: Username '{}' already taken", username);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        if (userRepository.existsByEmail(email)) {
            log.warn("Registration failed: Email '{}' already in use", email);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        try {
            User u = new User();
            u.setName(name);
            u.setSurname(surname);
            u.setUsername(username);
            u.setEmail(email);
            u.setPasswordHash(passwordEncoder.encode(rawPassword));

            User saved = userRepository.save(u);

            String token = jwtService.generateToken(saved.getId(), saved.getUsername(), saved.getEmail());

            log.info("User registered successfully: {} (ID: {})", username, saved.getId());

            return new AuthResponse(
                    token,
                    saved.getId(),
                    saved.getUsername(),
                    saved.getEmail(),
                    saved.getName(),
                    saved.getSurname(),
                    saved.getCreatedAt()
            );
        } catch (Exception e) {
            log.error("Registration failed for username: {}", username, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed");
        }
    }

    public AuthResponse login(LoginRequest req) {
        String id = req.identifier().trim().toLowerCase();
        String raw = req.password();

        log.info("Login attempt for identifier: {}", id);

        try {
            var userOpt = id.contains("@")
                    ? userRepository.findByEmail(id)
                    : userRepository.findByUsername(id);

            var user = userOpt.orElseThrow(() -> {
                log.warn("Login failed: User not found for identifier: {}", id);
                return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            });

            if (!passwordEncoder.matches(raw, user.getPasswordHash())) {
                log.warn("Login failed: Invalid password for identifier: {}", id);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }

            String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getEmail());

            log.info("User logged in successfully: {} (ID: {})", user.getUsername(), user.getId());

            return new AuthResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getName(),
                    user.getSurname(),
                    user.getCreatedAt()
            );
        } catch (ResponseStatusException e) {
            throw e; // Re-throw expected exceptions
        } catch (Exception e) {
            log.error("Login failed for identifier: {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Login failed");
        }
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("Password change attempt for user ID: {}", userId);

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User not found for ID: {}", userId);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                    });

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                log.warn("Password change failed: Invalid current password for user ID: {}", userId);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current password is incorrect");
            }

            // Check that new password is different from current password
            if (currentPassword.equals(newPassword)) {
                log.warn("Password change failed: New password is same as current password for user ID: {}", userId);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from current password");
            }

            // Update to new password
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            log.info("Password changed successfully for user ID: {}", userId);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Password change failed for user ID: {}", userId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to change password");
        }
    }
}