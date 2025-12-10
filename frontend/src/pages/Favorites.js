import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import ConfirmationModal from "../components/ConfirmationModal";
import api from "../api/axios";
import { imgUrl, ConfigApi } from "../api/tmdbApi";
import styles from "./Favorites.module.css";

export default function Favorites() {
    const [favoritesWithDetails, setFavoritesWithDetails] = useState([]);
    const [loading, setLoading] = useState(true);
    const [deleteModal, setDeleteModal] = useState(null);
    const [successModal, setSuccessModal] = useState(false);
    const navigate = useNavigate();

    const loadFavorites = useCallback(async () => {
        try {
            const { data } = await api.get("/api/favorites");

            // Fetch details from backend proxy for each favorite
            const detailsPromises = data.map(async (fav) => {
                try {
                    const endpoint = fav.mediaType === "movie"
                        ? `/api/tmdb/movies/${fav.tmdbId}`
                        : `/api/tmdb/tv/${fav.tmdbId}`;

                    const response = await api.get(endpoint);
                    const details = response.data;

                    return {
                        ...fav,
                        genres: details.genres?.map(g => g.name).join(", ") || "—",
                        rating: Number(details.vote_average || 0).toFixed(1),
                        releaseDate: fav.mediaType === "movie"
                            ? details.release_date || "—"
                            : details.first_air_date || "—"
                    };
                } catch (err) {
                    return {
                        ...fav,
                        genres: "—",
                        rating: "—",
                        releaseDate: "—"
                    };
                }
            });

            const withDetails = await Promise.all(detailsPromises);
            setFavoritesWithDetails(withDetails);
        } catch (err) {
            console.error("Failed to load favorites:", err);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        ConfigApi.loadOnce();
        loadFavorites();
    }, [loadFavorites]);

    const handleDelete = async () => {
        if (!deleteModal) return;

        try {
            await api.delete(`/api/favorites/${deleteModal.id}`);
            setFavoritesWithDetails((prev) => prev.filter((fav) => fav.id !== deleteModal.id));
            setDeleteModal(null);
            setSuccessModal(true);
        } catch (err) {
            alert("Failed to remove from favorites");
        }
    };

    const handleCardClick = (favorite) => {
        navigate(`/${favorite.mediaType}/${favorite.tmdbId}`);
    };

    if (loading) {
        return (
            <div className={styles.page}>
                <Navbar />
                <div className={styles.loadingWrapper}>
                    <div className={styles.spinner}></div>
                    <p>Loading favorites...</p>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.page}>
            <Navbar />

            <div className={styles.container}>
                <h1 className={styles.pageTitle}>My Favorites</h1>

                {favoritesWithDetails.length === 0 ? (
                    <div className={styles.emptyState}>
                        <div className={styles.emptyIcon}>♡</div>
                        <h2>No favorites yet</h2>
                        <p>Start adding movies and TV shows you love!</p>
                    </div>
                ) : (
                    <div className={styles.list}>
                        {favoritesWithDetails.map((fav) => (
                            <div key={fav.id} className={styles.listItem}>
                                <div
                                    className={styles.poster}
                                    onClick={() => handleCardClick(fav)}
                                >
                                    {fav.posterPath ? (
                                        <img src={imgUrl(fav.posterPath, "w342")} alt={fav.title} />
                                    ) : (
                                        <div className={styles.noPoster}>No Image</div>
                                    )}
                                </div>

                                <div className={styles.details}>
                                    <div className={styles.titleRow}>
                                        <h2 className={styles.title}>{fav.title}</h2>
                                        <span className={styles.typeBadge}>
                                            {fav.mediaType === "tv" ? "TV Show" : "Movie"}
                                        </span>
                                    </div>

                                    <div className={styles.releaseDate}>
                                        {fav.releaseDate}
                                    </div>

                                    <div className={styles.rating}>
                                        ★ {fav.rating}
                                    </div>
                                </div>

                                <button
                                    className={styles.removeBtn}
                                    onClick={() => setDeleteModal(fav)}
                                >
                                    Remove
                                </button>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Delete Confirmation Modal */}
            {deleteModal && (
                <ConfirmationModal
                    type="confirm"
                    message={`Are you sure you want to remove "${deleteModal.title}" from your favorites?`}
                    confirmText="Remove"
                    cancelText="Cancel"
                    onConfirm={handleDelete}
                    onClose={() => setDeleteModal(null)}
                />
            )}

            {/* Success Modal */}
            {successModal && (
                <ConfirmationModal
                    message="Successfully removed from favorites!"
                    onClose={() => setSuccessModal(false)}
                />
            )}
        </div>
    );
}
