import api from "./axios";

// ========================================
// CONFIGURATION & HELPERS
// ========================================

/**
 * @typedef {Object} TMDBConfig
 * @property {{secure_base_url: string}} images
 */

/**
 * @typedef {Object} TMDBGenre
 * @property {number} id
 * @property {string} name
 */

/**
 * @typedef {Object} TMDBGenreList
 * @property {TMDBGenre[]} genres
 */

/**
 * API configuration helper for TMDB image URLs and genre mappings.
 *
 * Loads configuration and genre data from TMDB on first use.
 * Note: Uses TMDB API key directly for public endpoints (config, genres)
 * which is acceptable as these are read-only, public data.
 */
export const ConfigApi = {
    _loaded: false,
    _imgBase: "https://image.tmdb.org/t/p/",
    _genresMovie: {},
    _genresTv: {},

    imgBase(size = "w500") {
        return `${this._imgBase}${size}`;
    },

    genreName(kind, id) {
        if (kind === "tv") return this._genresTv[id] || "";
        return this._genresMovie[id] || "";
    },

    /**
     * Loads TMDB configuration and genre lists once.
     *
     * NOTE: This method uses the TMDB API key directly for fetching public data
     * (configuration and genre lists). This is acceptable because:
     * 1. These are read-only, public endpoints
     * 2. No user-specific or sensitive data is accessed
     * 3. Fetching from backend would add unnecessary complexity for static data
     */
    async loadOnce() {
        if (this._loaded) return;
        try {
            // TMDB API key only for public config/genre endpoints (safe to use client-side)
            const TMDB_KEY = "e49077fc896987df22fed2168c5d7d1a";

            /** @type {[TMDBConfig, TMDBGenreList, TMDBGenreList]} */
            const [cfg, movieGenres, tvGenres] = await Promise.all([
                fetch(`https://api.themoviedb.org/3/configuration?api_key=${TMDB_KEY}`).then(r => r.json()),
                fetch(`https://api.themoviedb.org/3/genre/movie/list?language=en-US&api_key=${TMDB_KEY}`).then(r => r.json()),
                fetch(`https://api.themoviedb.org/3/genre/tv/list?language=en-US&api_key=${TMDB_KEY}`).then(r => r.json())
            ]);

            if (cfg?.images?.secure_base_url) {
                this._imgBase = cfg.images.secure_base_url;
            }

            if (Array.isArray(movieGenres?.genres)) {
                this._genresMovie = Object.fromEntries(
                    movieGenres.genres.map(g => [g.id, g.name])
                );
            }

            if (Array.isArray(tvGenres?.genres)) {
                this._genresTv = Object.fromEntries(
                    tvGenres.genres.map(g => [g.id, g.name])
                );
            }

            this._loaded = true;
        } catch (err) {
            // Silently fail - genres will just not be displayed
            // In production, consider using a proper logging service
        }
    }
};

export const imgUrl = (path, size = "w342") =>
    path ? `${ConfigApi.imgBase(size)}${path}` : "";

// ========================================
// BACKEND PROXY HELPER
// ========================================

/**
 * Helper function to call TMDB endpoints through our backend proxy.
 *
 * All movie/TV data requests go through the backend to keep the API key secure.
 *
 * @param {string} endpoint - The API endpoint path (e.g., "/movies/popular")
 * @param {Object} params - Query parameters
 * @returns {Promise<Object>} The API response data
 */
const backendTmdb = (endpoint, params = {}) =>
    api.get(`/api/tmdb${endpoint}`, { params })
        .then(r => r.data)
        .catch(err => {
            // Re-throw error to be handled by caller
            throw err;
        });

// ========================================
// API CALLS - ALL GO THROUGH BACKEND
// ========================================

/**
 * API methods for fetching movie data.
 */
export const MoviesApi = {
    popular:  (page = 1) => backendTmdb("/movies/popular", { page }),
    latest:   (page = 1) => backendTmdb("/movies/latest", { page }),
    topRated: (page = 1) => backendTmdb("/movies/top-rated", { page }),
    search:   (q, page = 1) => backendTmdb("/movies/search", { q, page }),
    details:  (id) => backendTmdb(`/movies/${id}`)
};

/**
 * API methods for fetching TV show data.
 */
export const TvApi = {
    popular:  (page = 1) => backendTmdb("/tv/popular", { page }),
    latest:   (page = 1) => backendTmdb("/tv/latest", { page }),
    topRated: (page = 1) => backendTmdb("/tv/top-rated", { page }),
    search:   (q, page = 1) => backendTmdb("/tv/search", { q, page }),
    details:  (id) => backendTmdb(`/tv/${id}`)
};

/**
 * API methods for multi-search (movies and TV shows combined).
 */
export const MultiApi = {
    search: (q, page = 1) => backendTmdb("/multi/search", { q, page })
};

/**
 * API methods for the main feed (trending content, mixed movies and TV).
 */
export const FeedApi = {
    popular:  (page = 1) => backendTmdb("/feed/popular", { page }),
    latest:   (page = 1) => backendTmdb("/feed/latest", { page }),
    topRated: (page = 1) => backendTmdb("/feed/top-rated", { page })
};
