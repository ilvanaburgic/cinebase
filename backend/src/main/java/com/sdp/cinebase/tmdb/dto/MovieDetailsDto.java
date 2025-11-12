package com.sdp.cinebase.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDetailsDto(
        int id,
        String title,
        String name,
        String overview,
        String poster_path,
        String backdrop_path,
        Double vote_average,
        Integer vote_count,
        String release_date,
        String first_air_date,
        Integer runtime,
        List<Genre> genres,
        Credits credits,
        Videos videos,
        List<Season> seasons,
        List<Creator> created_by,
        Reviews reviews
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Genre(int id, String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Credits(List<Cast> cast, List<Crew> crew) {
        //Default constructor for null case
        public Credits {
            if (cast == null) cast = List.of();
            if (crew == null) crew = List.of();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cast(
            int id,
            String name,
            String character,
            String profile_path,
            Integer order
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Crew(
            int id,
            String name,
            String job,
            String department
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Videos(List<Video> results) {
        public Videos {
            if (results == null) results = List.of();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Video(
            String id,
            String key,
            String name,
            String site,
            String type
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Season(
            int id,
            String name,
            Integer season_number,
            Integer episode_count,
            String air_date
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Creator(
            int id,
            String name,
            String credit_id,
            Integer gender,
            String profile_path
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Reviews(List<Review> results) {
        public Reviews {
            if (results == null) results = List.of();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Review(
            String id,
            String author,
            String content,
            String created_at,
            AuthorDetails author_details
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AuthorDetails(
            String name,
            String username,
            String avatar_path,
            Double rating
    ) {}
}