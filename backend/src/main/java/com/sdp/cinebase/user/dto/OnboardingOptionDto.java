package com.sdp.cinebase.user.dto;

public record OnboardingOptionDto(
        Integer tmdbId,
        String mediaType,
        String title,
        String posterPath,
        String overview,
        Double voteAverage,
        String[] genres
) {}
