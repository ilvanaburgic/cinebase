package com.sdp.cinebase.user.dto;

import java.time.Instant;

public record UserDto(
        Long id,
        String username,
        String email,
        String name,
        String surname,
        Instant createdAt
) {}
