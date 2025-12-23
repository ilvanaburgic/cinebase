package com.sdp.cinebase.game.dto;

public record HigherLowerQuestionDto(
    Long id,
    String mediaType,
    Integer tmdbId,
    String title,
    String posterPath,
    String metric,
    Double value
) {}
