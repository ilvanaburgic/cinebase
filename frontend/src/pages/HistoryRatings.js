import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import ConfirmationModal from "../components/ConfirmationModal";
import api from "../api/axios";
import styles from "./HistoryRatings.module.css";

export default function HistoryRatings() {
    const navigate = useNavigate();
    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [deleteModal, setDeleteModal] = useState(null);
    const [deleting, setDeleting] = useState(false);

    useEffect(() => {
        void loadReviews();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const loadReviews = async () => {
        setLoading(true);
        try {
            const { data } = await api.get("/api/reviews/my");
            setReviews(data);
        } catch (err) {
            // Failed to load reviews
            setReviews([]);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (review) => {
        setDeleting(true);
        try {
            await api.delete(`/api/reviews/${review?.tmdbId}/${review?.mediaType}`);
            setDeleteModal(null);
            // Remove from list
            setReviews(reviews.filter(r => r?.id !== review?.id));
        } catch (err) {
            // Failed to delete review
            alert("Failed to delete review");
        } finally {
            setDeleting(false);
        }
    };

    const handleEdit = (review) => {
        navigate(`/review/${review?.mediaType}/${review?.tmdbId}?title=${encodeURIComponent(review?.title || '')}`);
    };

    const handleNavigateToMedia = (review) => {
        navigate(`/${review?.mediaType}/${review?.tmdbId}`);
    };

    if (loading) {
        return (
            <div className={styles.page}>
                <Navbar />
                <div className={styles.loading}>Loading your reviews...</div>
            </div>
        );
    }

    return (
        <div className={styles.page}>
            <Navbar />

            <div className={styles.container}>

                <h1 className={styles.title}>Your Rating History</h1>
                <p className={styles.subtitle}>
                    {reviews.length} {reviews.length === 1 ? "review" : "reviews"}
                </p>

                {reviews.length === 0 ? (
                    <div className={styles.emptyState}>
                        <div className={styles.emptyIcon}>★</div>
                        <h2>No reviews yet</h2>
                        <p>Start rating movies and TV shows to build your history!</p>
                        <button
                            className={styles.exploreBtn}
                            onClick={() => navigate("/dashboard")}
                        >
                            Explore Content
                        </button>
                    </div>
                ) : (
                    <div className={styles.reviewsList}>
                        {reviews.map(review => (
                            <div key={review?.id} className={styles.reviewCard}>
                                <div className={styles.cardHeader}>
                                    <div>
                                        <h3
                                            className={styles.reviewTitle}
                                            onClick={() => handleNavigateToMedia(review)}
                                        >
                                            {review?.title}
                                        </h3>
                                        <div className={styles.reviewMeta}>
                                            <span className={styles.mediaType}>
                                                {review?.mediaType === "tv" ? "TV Show" : "Movie"}
                                            </span>
                                            <span className={styles.reviewDate}>
                                                {new Date(review?.createdAt).toLocaleDateString("en-US", {
                                                    year: "numeric",
                                                    month: "short",
                                                    day: "numeric"
                                                })}
                                            </span>
                                            {review?.updatedAt && review?.updatedAt !== review?.createdAt && (
                                                <span className={styles.edited}>(edited)</span>
                                            )}
                                        </div>
                                    </div>
                                    {review?.rating && (
                                        <div className={styles.ratingBadge}>
                                            ★ {review?.rating}/10
                                        </div>
                                    )}
                                </div>

                                {review?.reviewText && (
                                    <p className={styles.reviewText}>
                                        {review?.reviewText}
                                    </p>
                                )}

                                <div className={styles.cardActions}>
                                    <button
                                        className={styles.editBtn}
                                        onClick={() => handleEdit(review)}
                                    >
                                        Edit
                                    </button>
                                    <button
                                        className={styles.deleteBtn}
                                        onClick={() => setDeleteModal(review)}
                                    >
                                        Delete
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Delete Confirmation Modal */}
            {deleteModal && (
                <ConfirmationModal
                    type="confirm"
                    message={`Are you sure you want to delete your review for "${deleteModal?.title}"?`}
                    confirmText={deleting ? "Deleting..." : "Delete"}
                    cancelText="Cancel"
                    onConfirm={() => handleDelete(deleteModal)}
                    onClose={() => setDeleteModal(null)}
                />
            )}
        </div>
    );
}
