import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import ConfirmationModal from "../components/ConfirmationModal";
import api from "../api/axios";
import { imgUrl, ConfigApi } from "../api/tmdbApi";
import styles from "./Watchlist.module.css";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBookmark } from '@fortawesome/free-regular-svg-icons';

export default function Watchlist() {
    const [watchlistWithDetails, setWatchlistWithDetails] = useState([]);
    const [loading, setLoading] = useState(true);
    const [deleteModal, setDeleteModal] = useState(null);
    const [successModal, setSuccessModal] = useState(false);
    const navigate = useNavigate();

    const loadWatchlist = useCallback(async () => {
        try {
            const { data } = await api.get("/api/watchlist");

            // Fetch details from backend proxy for each watchlist item
            const detailsPromises = data.map(async (item) => {
                try {
                    const endpoint = item.mediaType === "movie"
                        ? `/api/tmdb/movies/${item.tmdbId}`
                        : `/api/tmdb/tv/${item.tmdbId}`;

                    const response = await api.get(endpoint);
                    const details = response.data;

                    return {
                        ...item,
                        genres: details.genres?.map(g => g.name).join(", ") || "—",
                        rating: Number(details.vote_average || 0).toFixed(1),
                        releaseDate: item.mediaType === "movie"
                            ? details.release_date || "—"
                            : details.first_air_date || "—"
                    };
                } catch (err) {
                    return {
                        ...item,
                        genres: "—",
                        rating: "—",
                        releaseDate: "—"
                    };
                }
            });

            const withDetails = await Promise.all(detailsPromises);
            setWatchlistWithDetails(withDetails);
        } catch (err) {
            // Failed to load watchlist
            setWatchlistWithDetails([]);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        void ConfigApi.loadOnce();
        void loadWatchlist();
    }, [loadWatchlist]);

    const handleDelete = async () => {
        if (!deleteModal) return;

        try {
            await api.delete(`/api/watchlist/${deleteModal.id}`);
            setWatchlistWithDetails((prev) => prev.filter((item) => item.id !== deleteModal.id));
            setDeleteModal(null);
            setSuccessModal(true);
        } catch (err) {
            alert("Failed to remove from watchlist");
        }
    };

    const handleCardClick = (item) => {
        navigate(`/${item.mediaType}/${item.tmdbId}`);
    };

    if (loading) {
        return (
            <div className={styles.page}>
                <Navbar />
                <div className={styles.loadingWrapper}>
                    <div className={styles.spinner}></div>
                    <p>Loading watchlist...</p>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.page}>
            <Navbar />

            <div className={styles.container}>
                <h1 className={styles.pageTitle}>My Watchlist</h1>

                {watchlistWithDetails.length === 0 ? (
                    <div className={styles.emptyState}>
                        <div className={styles.emptyIcon}>
                            <FontAwesomeIcon icon={faBookmark} />
                        </div>
                        <h2>No items in watchlist yet</h2>
                        <p>Start adding movies and TV shows you want to watch!</p>
                    </div>
                ) : (
                    <div className={styles.list}>
                        {watchlistWithDetails.map((item) => (
                            <div key={item.id} className={styles.listItem}>
                                <div
                                    className={styles.poster}
                                    onClick={() => handleCardClick(item)}
                                >
                                    {item.posterPath ? (
                                        <img src={imgUrl(item.posterPath, "w342")} alt={item.title} />
                                    ) : (
                                        <div className={styles.noPoster}>No Image</div>
                                    )}
                                </div>

                                <div className={styles.details}>
                                    <div className={styles.titleRow}>
                                        <h2 className={styles.title}>{item.title}</h2>
                                        <span className={styles.typeBadge}>
                                            {item.mediaType === "tv" ? "TV Show" : "Movie"}
                                        </span>
                                    </div>

                                    <div className={styles.releaseDate}>
                                        {item.releaseDate}
                                    </div>

                                    <div className={styles.rating}>
                                        ★ {item.rating}
                                    </div>
                                </div>

                                <button
                                    className={styles.removeBtn}
                                    onClick={() => setDeleteModal(item)}
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
                    message={`Are you sure you want to remove "${deleteModal.title}" from your watchlist?`}
                    confirmText="Remove"
                    cancelText="Cancel"
                    onConfirm={handleDelete}
                    onClose={() => setDeleteModal(null)}
                />
            )}

            {/* Success Modal */}
            {successModal && (
                <ConfirmationModal
                    message="Successfully removed from watchlist!"
                    onClose={() => setSuccessModal(false)}
                />
            )}
        </div>
    );
}
