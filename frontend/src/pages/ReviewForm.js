import { useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import api from "../api/axios";
import styles from "./ReviewForm.module.css";

export default function ReviewForm() {
    const { tmdbId, mediaType } = useParams();
    const [searchParams] = useSearchParams();
    const title = searchParams.get("title") || "Unknown";
    const navigate = useNavigate();

    const [rating, setRating] = useState(null);
    const [hoverRating, setHoverRating] = useState(null);
    const [reviewText, setReviewText] = useState("");
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");
    const [isEditMode, setIsEditMode] = useState(false);

    useEffect(() => {
        void checkExistingReview();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const checkExistingReview = async () => {
        try {
            const { data } = await api.get(`/api/reviews/my/${tmdbId}/${mediaType}`);
            // Review exists - edit mode
            setRating(data.rating);
            setReviewText(data.reviewText || "");
            setIsEditMode(true);
        } catch (err) {
            // No existing review - create mode
            setIsEditMode(false);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        // Validation
        if (!rating && !reviewText.trim()) {
            setError("Please provide a rating or write a review");
            return;
        }

        if (rating && (rating < 1 || rating > 10)) {
            setError("Rating must be between 1 and 10");
            return;
        }

        setSubmitting(true);

        try {
            const payload = {
                tmdbId: parseInt(tmdbId),
                mediaType,
                title,
                rating,
                reviewText: reviewText.trim() || null
            };

            if (isEditMode) {
                await api.put(`/api/reviews/${tmdbId}/${mediaType}`, payload);
            } else {
                await api.post("/api/reviews", payload);
            }

            // Navigate back to movie details
            navigate(`/${mediaType}/${tmdbId}`);
        } catch (err) {
            setError(err?.response?.data?.message || "Failed to save review");
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) {
        return (
            <div className={styles.page}>
                <Navbar />
                <div className={styles.loading}>Loading...</div>
            </div>
        );
    }

    return (
        <div className={styles.page}>
            <Navbar />

            <div className={styles.container}>

                <h1 className={styles.title}>
                    {isEditMode ? "Edit Your Review" : "Write a Review"}
                </h1>
                <p className={styles.subtitle}>{title}</p>

                <form className={styles.form} onSubmit={handleSubmit}>
                    {/* Star Rating */}
                    <div className={styles.section}>
                        <label className={styles.label}>Your Rating (optional)</label>
                        <div className={styles.stars}>
                            {[...Array(10)].map((_, index) => {
                                const starValue = index + 1;
                                return (
                                    <button
                                        key={starValue}
                                        type="button"
                                        className={`${styles.star} ${
                                            starValue <= (hoverRating || rating) ? styles.active : ""
                                        }`}
                                        onClick={() => setRating(starValue)}
                                        onMouseEnter={() => setHoverRating(starValue)}
                                        onMouseLeave={() => setHoverRating(null)}
                                    >
                                        ★
                                    </button>
                                );
                            })}
                            {rating && (
                                <span className={styles.ratingText}>{rating}/10</span>
                            )}
                        </div>
                    </div>

                    {/* Review Text */}
                    <div className={styles.section}>
                        <label className={styles.label} htmlFor="reviewText">
                            Your Review (optional)
                        </label>
                        <textarea
                            id="reviewText"
                            className={styles.textarea}
                            placeholder="Share your thoughts about this movie/show..."
                            value={reviewText}
                            onChange={(e) => setReviewText(e.target.value)}
                            rows={8}
                        />
                    </div>

                    {error && <div className={styles.error}>{error}</div>}

                    <div className={styles.actions}>
                        <button
                            type="button"
                            className={styles.cancelBtn}
                            onClick={() => navigate(-1)}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className={styles.submitBtn}
                            disabled={submitting}
                        >
                            {submitting ? "Saving..." : isEditMode ? "Update Review" : "Submit Review"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
