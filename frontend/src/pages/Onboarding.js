import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { imgUrl, ConfigApi } from "../api/tmdbApi";
import styles from "./Onboarding.module.css";

export default function Onboarding() {
    const navigate = useNavigate();
    const [options, setOptions] = useState([]);
    const [selectedPicks, setSelectedPicks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        const fetchOptions = async () => {
            setLoading(true);
            setError(null);
            try {
                await ConfigApi.loadOnce();
                const { data } = await api.get("/api/preferences/onboarding-options");
                setOptions(data);
            } catch (err) {
                console.error("Failed to load onboarding options:", err);
                setError("Failed to load options. Please try again.");
            } finally {
                setLoading(false);
            }
        };

        void fetchOptions();
    }, []);

    const handleToggleSelection = (option) => {
        const isAlreadySelected = selectedPicks.some(pick => pick.tmdbId === option.tmdbId);

        if (isAlreadySelected) {
            // Deselect
            setSelectedPicks(selectedPicks.filter(pick => pick.tmdbId !== option.tmdbId));
        } else {
            // Select (max 4)
            if (selectedPicks.length < 4) {
                setSelectedPicks([...selectedPicks, option]);
            }
        }
    };

    const handleSubmit = async () => {
        if (selectedPicks.length !== 4) {
            alert("Please select exactly 4 favorites!");
            return;
        }

        setSubmitting(true);
        try {
            const picks = selectedPicks.map(pick => ({
                tmdbId: pick.tmdbId,
                mediaType: pick.mediaType,
                title: pick.title,
                genres: JSON.stringify(pick.genres || [])
            }));

            await api.post("/api/preferences/save-picks", { picks });
            navigate("/dashboard");
        } catch (err) {
            console.error("Failed to save picks:", err);
            alert("Failed to save your selections. Please try again.");
        } finally {
            setSubmitting(false);
        }
    };

    const handleSkip = () => {
        // Submit empty picks to mark onboarding as complete
        const submitEmpty = async () => {
            try {
                await api.post("/api/preferences/save-picks", { picks: [] });
                navigate("/dashboard");
            } catch (err) {
                console.error("Failed to skip onboarding:", err);
                navigate("/dashboard");
            }
        };
        void submitEmpty();
    };

    if (loading) {
        return (
            <div className={styles.page}>
                <div className={styles.loading}>Loading...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className={styles.page}>
                <div className={styles.error}>
                    <p>{error}</p>
                    <button onClick={() => window.location.reload()}>Retry</button>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.page}>
            <div className={styles.container}>
                <div className={styles.header}>
                    <h1 className={styles.title}>Welcome to CineBase!</h1>
                    <p className={styles.subtitle}>
                        Select <span className={styles.highlight}>4 favorite</span> movies or series to get personalized recommendations
                    </p>
                    <div className={styles.counter}>
                        {selectedPicks.length} / 4 selected
                    </div>
                </div>

                <div className={styles.grid}>
                    {options.map((option) => {
                        const isSelected = selectedPicks.some(pick => pick.tmdbId === option.tmdbId);
                        return (
                            <div
                                key={`${option.mediaType}-${option.tmdbId}`}
                                className={`${styles.card} ${isSelected ? styles.selected : ""}`}
                                onClick={() => handleToggleSelection(option)}
                            >
                                {option.posterPath ? (
                                    <img
                                        src={imgUrl(option.posterPath, "w342")}
                                        alt={option.title}
                                        className={styles.poster}
                                    />
                                ) : (
                                    <div className={styles.noPoster}>No Image</div>
                                )}
                                <div className={styles.cardInfo}>
                                    <div className={styles.cardTitle}>{option.title}</div>
                                    <div className={styles.mediaType}>
                                        {option.mediaType === "movie" ? "Movie" : "TV Series"}
                                    </div>
                                </div>
                                {isSelected && (
                                    <div className={styles.checkmark}>✓</div>
                                )}
                            </div>
                        );
                    })}
                </div>

                <div className={styles.actions}>
                    <button
                        className={styles.skipBtn}
                        onClick={handleSkip}
                        disabled={submitting}
                    >
                        Skip for now
                    </button>
                    <button
                        className={styles.submitBtn}
                        onClick={handleSubmit}
                        disabled={selectedPicks.length !== 4 || submitting}
                    >
                        {submitting ? "Saving..." : "Continue"}
                    </button>
                </div>
            </div>
        </div>
    );
}
