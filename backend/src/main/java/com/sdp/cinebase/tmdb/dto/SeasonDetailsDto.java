package com.sdp.cinebase.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SeasonDetailsDto(
        int id,
        String name,
        String overview,
        String poster_path,
        int season_number,
        String air_date,
        List<Episode> episodes
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Episode(
            int id,
            String name,
            String overview,
            int episode_number,
            String air_date,
            String still_path,
            Double vote_average,
            Integer runtime
    ) {}
}