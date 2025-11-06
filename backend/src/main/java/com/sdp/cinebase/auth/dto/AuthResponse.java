package com.sdp.cinebase.auth.dto;

public record AuthResponse(
        String token,
        Long id,
        String username,
        String email,
        String name,
        String surname
) {}
