package com.sdp.cinebase.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PersonDetailsDto(
        int id,
        String name,
        String biography,
        String birthday,
        String place_of_birth,
        String profile_path,
        String known_for_department,
        MovieCredits movie_credits,
        TvCredits tv_credits
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MovieCredits(List<Cast> cast) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TvCredits(List<Cast> cast) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cast(
            int id,
            String title,
            String name,
            String character,
            String poster_path,
            String release_date,
            String first_air_date,
            Double vote_average
    ) {}
}