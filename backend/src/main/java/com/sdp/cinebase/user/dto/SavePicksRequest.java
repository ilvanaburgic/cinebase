package com.sdp.cinebase.user.dto;

import java.util.List;

public record SavePicksRequest(List<PickItem> picks) {
    public record PickItem(
            Long tmdbId,
            String mediaType,
            String title,
            String genres
    ) {}
}
