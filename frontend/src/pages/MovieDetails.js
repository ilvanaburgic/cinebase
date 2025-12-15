import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { MoviesApi, TvApi, imgUrl } from "../api/tmdbApi";
import Navbar from "../components/Navbar";
import api from "../api/axios";
import styles from "./MovieDetails.module.css";

/**
 * @typedef {Object} MovieDetailsResponse
 * @property {number} id
 * @property {string} [title]
 * @property {string} [name]
 * @property {string} [overview]
 * @property {string} [poster_path]
 * @property {string} [backdrop_path]
 * @property {number} [vote_average]
 * @property {number} [vote_count]
 * @property {string} [release_date]
 * @property {string} [first_air_date]
 * @property {number} [runtime]
 * @property {Array<{id: number, name: string}>} [genres]
 * @property {{cast: Array<Cast>, crew: Array<Crew>}} [credits]
 * @property {{results: Array<Video>}} [videos]
 * @property {Array<Season>} [seasons]
 * @property {Array<Creator>} [created_by]
 * @property {{results: Array<Review>}} [reviews]
 */

/**
 * @typedef {Object} Cast
 * @property {number} id
 * @property {string} name
 * @property {string} character
 * @property {string} [profile_path]
 * @property {number} [order]
 */

/**
 * @typedef {Object} Crew
 * @property {number} id
 * @property {string} name
 * @property {string} job
 * @property {string} department
 */

/**
 * @typedef {Object} Video
 * @property {string} id
 * @property {string} key
 * @property {string} name
 * @property {string} site
 * @property {string} type
 */

/**
 * @typedef {Object} Season
 * @property {number} id
 * @property {string} name
 * @property {number} season_number
 * @property {number} episode_count
 * @property {string} [air_date]
 */

/**
 * @typedef {Object} Creator
 * @property {number} id
 * @property {string} name
 * @property {string} credit_id
 * @property {number} [gender]
 * @property {string} [profile_path]
 */

/**
 * @typedef {Object} Review
 * @property {string} id
 * @property {string} author
 * @property {string} content
 * @property {string} created_at
 * @property {AuthorDetails} [author_details]
 */

/**
 * @typedef {Object} AuthorDetails
 * @property {string} [name]
 * @property {string} username
 * @property {string} [avatar_path]
 * @property {number} [rating]
 */

export default function MovieDetails() {
    const { id } = useParams();
    const type = window.location.pathname.startsWith("/tv") ? "tv" : "movie";
    const navigate = useNavigate();
    /** @type {[MovieDetailsResponse | null, Function]} */
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [userReviews, setUserReviews] = useState([]);
    const [myReview, setMyReview] = useState(null);

    useEffect(() => {
        async function fetchDetails() {
            setLoading(true);
            setError("");
            try {
                const response = type === "tv"
                    ? await TvApi.details(id)
                    : await MoviesApi.details(id);
                setData(response);
            } catch (err) {
                // Failed to fetch details
                setError("Failed to load details");
            } finally {
                setLoading(false);
            }
        }

        async function fetchUserReviews() {
            try {
                const { data } = await api.get(`/api/reviews/${id}/${type}`);
                setUserReviews(data);
            } catch (err) {
                // Failed to fetch user reviews
                setUserReviews([]);
            }
        }

        async function fetchMyReview() {
            try {
                const { data } = await api.get(`/api/reviews/my/${id}/${type}`);
                setMyReview(data);
            } catch (err) {
                // No review from this user
                setMyReview(null);
            }
        }

        void fetchDetails();
        void fetchUserReviews();
        void fetchMyReview();
    }, [id, type]);

    if (loading) {
        return <div className={styles.loading}>Loading...</div>;
    }

    if (error || !data) {
        return (
            <div className={styles.error}>
                <p>{error || "Content not found"}</p>
                <button onClick={() => navigate(-1)}>Go Back</button>
            </div>
        );
    }

    const title = data.title || data.name;
    const releaseDate = data.release_date || data.first_air_date;
    const poster = imgUrl(data.poster_path, "w500");
    const backdrop = imgUrl(data.backdrop_path, "original");
    const genres = data.genres?.map(g => g.name).join(", ") || "—";

    // For TV shows, show creators instead of director/writers
    const creators = data.created_by?.map(c => c.name).join(", ") || null;
    const director = data.credits?.crew?.find(c => c.job === "Director")?.name || "—";
    const writers = data.credits?.crew
        ?.filter(c => c.job === "Writer" || c.job === "Screenplay")
        ?.slice(0, 2)
        ?.map(c => c.name)
        .join(", ") || "—";

    const cast = data.credits?.cast?.slice(0, 6) || [];
    const trailer = data.videos?.results?.find(v => v.type === "Trailer" && v.site === "YouTube");
    const reviews = data.reviews?.results?.slice(0, 3) || [];

    return (
        <div className={styles.page}>
            {/* Navigation - identical to Dashboard */}
            <Navbar
                active={type === "tv" ? "tv" : "movies"}
                onTab={() => navigate('/dashboard')}
            />

            {/* Hero Section */}
            <section
                className={styles.hero}
                style={{backgroundImage: backdrop ? `url(${backdrop})` : "none"}}
            >
                <div className={styles.heroOverlay}>
                    <div className={styles.heroContent}>
                        <div className={styles.posterWrapper}>
                            {poster ? (
                                <img src={poster} alt={title} className={styles.poster}/>
                            ) : (
                                <div className={styles.posterPlaceholder}>No Image</div>
                            )}
                        </div>

                        <div className={styles.info}>
                            <h1 className={styles.title}>{title}</h1>

                            <div className={styles.meta}>
                                <span className={styles.rating}>★ {data.vote_average?.toFixed(1)}</span>
                                <span>{genres}</span>
                                <span>{releaseDate ? new Date(releaseDate).getFullYear() : "—"}</span>
                                {data.runtime && <span>{data.runtime} min</span>}
                            </div>

                            <div className={styles.crew}>
                                {type === "tv" && creators ? (
                                    <div><strong>Creators:</strong> {creators}</div>
                                ) : (
                                    <>
                                        <div><strong>Director:</strong> {director}</div>
                                        <div><strong>Writers:</strong> {writers}</div>
                                    </>
                                )}
                            </div>

                            <p className={styles.overview}>
                                {data.overview || "No description available."}
                            </p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Trailer */}
            {trailer && (
                <section className={styles.trailerSection}>
                    <h2>Trailer</h2>
                    <div className={styles.trailerWrapper}>
                        <iframe
                            width="100%"
                            height="500"
                            src={`https://www.youtube.com/embed/${trailer.key}`}
                            title="Trailer"
                            frameBorder="0"
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                            allowFullScreen
                        />
                    </div>
                </section>
            )}

            {/* Seasons (only for TV shows, filter out season 0) */}
            {type === "tv" && data.seasons && data.seasons.filter(s => s.season_number !== 0).length > 0 && (
                <section className={styles.seasonsSection}>
                    <h2>Seasons</h2>
                    <div className={styles.seasonsList}>
                        {data.seasons
                            .filter(season => season.season_number !== 0)
                            .map(season => (
                                <button
                                    key={season.id}
                                    className={styles.seasonItem}
                                    onClick={() => navigate(`/tv/${id}/season/${season.season_number}`)}
                                    type="button"
                                >
                                    <div className={styles.seasonNumber}>Season {season.season_number}</div>
                                    <div className={styles.seasonInfo}>
                                        {season.episode_count} episodes
                                        {season.air_date && ` • ${new Date(season.air_date).getFullYear()}`}
                                    </div>
                                </button>
                            ))}
                    </div>
                </section>
            )}

            {/* Cast */}
            {cast.length > 0 && (
                <section className={styles.castSection}>
                    <h2>Cast</h2>
                    <div className={styles.castGrid}>
                        {cast.map(actor => (
                            <div key={actor.id} className={styles.castCard}
                                 onClick={() => navigate(`/person/${actor.id}`)}
                                 style={{ cursor: 'pointer' }}
                            >
                                {actor.profile_path ? (
                                    <img
                                        src={imgUrl(actor.profile_path, "w185")}
                                        alt={actor.name}
                                        className={styles.castPhoto}
                                    />
                                ) : (
                                    <div className={styles.castPhotoPlaceholder}>?</div>
                                )}
                                <div className={styles.castInfo}>
                                    <div className={styles.castName}>{actor.name}</div>
                                    <div className={styles.castCharacter}>
                                        {actor.character ? `${actor.character}` : "—"}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </section>
            )}

            {/* Reviews */}
            <section className={styles.reviewsSection}>
                <div className={styles.reviewsHeader}>
                    <h2>Reviews</h2>
                    {myReview ? (
                        <button
                            className={styles.editReviewBtn}
                            onClick={() => navigate(`/review/${type}/${id}?title=${encodeURIComponent(title)}`)}
                        >
                            Edit Your Review
                        </button>
                    ) : (
                        <button
                            className={styles.writeReviewBtn}
                            onClick={() => navigate(`/review/${type}/${id}?title=${encodeURIComponent(title)}`)}
                        >
                            Write a Review
                        </button>
                    )}
                </div>

                {/* User Reviews */}
                {userReviews.length > 0 && (
                    <>
                        <h3 className={styles.reviewsSubheading}>User Reviews</h3>
                        <div className={styles.reviewsList}>
                            {userReviews.map(review => (
                                <div key={review.id} className={styles.reviewCard}>
                                    <div className={styles.reviewHeader}>
                                        <strong className={styles.reviewAuthor}>{review.username}</strong>
                                        {review.rating && (
                                            <span className={styles.reviewRating}>★ {review.rating}/10</span>
                                        )}
                                    </div>
                                    {review.reviewText && (
                                        <p className={styles.reviewContent}>
                                            {review.reviewText.length > 400
                                                ? `${review.reviewText.substring(0, 400)}...`
                                                : review.reviewText}
                                        </p>
                                    )}
                                    <div className={styles.reviewDate}>
                                        {new Date(review.createdAt).toLocaleDateString("en-US", {
                                            year: "numeric",
                                            month: "long",
                                            day: "numeric"
                                        })}
                                        {review?.updatedAt && review?.updatedAt !== review?.createdAt && (
                                            <span className={styles.edited}> (edited)</span>
                                        )}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </>
                )}

                {/* TMDB Reviews */}
                {reviews.length > 0 && (
                    <>
                        <h3 className={styles.reviewsSubheading}>TMDB Reviews</h3>
                        <div className={styles.reviewsList}>
                            {reviews.map(review => (
                                <div key={review.id} className={styles.reviewCard}>
                                    <div className={styles.reviewHeader}>
                                        <strong className={styles.reviewAuthor}>{review.author}</strong>
                                        {review.author_details?.rating && (
                                            <span className={styles.reviewRating}>★ {review.author_details.rating.toFixed(1)}</span>
                                        )}
                                    </div>
                                    <p className={styles.reviewContent}>
                                        {review.content.length > 400
                                            ? `${review.content.substring(0, 400)}...`
                                            : review.content}
                                    </p>
                                    <div className={styles.reviewDate}>
                                        {new Date(review.created_at).toLocaleDateString("en-US", {
                                            year: "numeric",
                                            month: "long",
                                            day: "numeric"
                                        })}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </>
                )}

                {userReviews.length === 0 && reviews.length === 0 && (
                    <p className={styles.reviewsPlaceholder}>
                        No reviews available yet. Be the first to write one!
                    </p>
                )}
            </section>

            {/* Footer */}
            <footer className={styles.footer}>© 2025 CineBase. All rights reserved.</footer>
        </div>
    );
}