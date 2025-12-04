package com.sdp.cinebase.auth.dto;

import java.time.Instant;

public record AuthResponse(
        String token,
        Long id,
        String username,
        String email,
        String name,
        String surname,
        Instant createdAt
) {}
