package com.sdp.cinebase.auth.web;

import com.sdp.cinebase.auth.dto.AuthResponse;
import com.sdp.cinebase.auth.dto.ChangePasswordRequest;
import com.sdp.cinebase.auth.dto.LoginRequest;
import com.sdp.cinebase.auth.dto.RegisterRequest;
import com.sdp.cinebase.auth.service.AuthService;
import com.sdp.cinebase.security.UserPrincipal;
import com.sdp.cinebase.user.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.sdp.cinebase.user.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        AuthResponse res = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal UserPrincipal me) {
        return userService.getById(me.getId());
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest req) {
        authService.changePassword(Long.parseLong(principal.getId()), req.currentPassword(), req.newPassword());
        return ResponseEntity.ok().build();
    }
}
