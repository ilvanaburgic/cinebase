import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { TvApi, imgUrl } from "../api/tmdbApi";
import Navbar from "../components/Navbar";
import styles from "./SeasonDetails.module.css";

export default function SeasonDetails() {
    const { id, seasonNumber } = useParams();
    const navigate = useNavigate();
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        async function fetchSeason() {
            setLoading(true);
            setError("");
            try {
                const response = await TvApi.season(id, seasonNumber);
                setData(response);
            } catch (err) {
                console.error("Error fetching season:", err);
                setError(err?.message || "Failed to load season details");
            } finally {
                setLoading(false);
            }
        }
        void fetchSeason();
    }, [id, seasonNumber]);

    if (loading) return <div className={styles.loading}>Loading...</div>;
    if (error || !data) {
        return (
            <div className={styles.error}>
                <p>{error || "Season not found"}</p>
                <button onClick={() => navigate(`/tv/${id}`)}>Back to Show</button>
            </div>
        );
    }

    return (
        <div className={styles.page}>
            <Navbar active="tv" onTab={() => navigate('/dashboard')} />

            <header className={styles.header}>
                <button className={styles.backBtn} onClick={() => navigate(`/tv/${id}`)}>
                    ← Back to Show
                </button>
                <h1>{data.name}</h1>
            </header>

            <section className={styles.episodes}>
                {data.episodes?.map(ep => (
                    <div key={ep.id} className={styles.episodeCard}>
                        <div className={styles.episodeImage}>
                            {ep.still_path ? (
                                <img src={imgUrl(ep.still_path, "w300")} alt={ep.name} />
                            ) : (
                                <div className={styles.placeholder}>No Image</div>
                            )}
                        </div>
                        <div className={styles.episodeInfo}>
                            <h3>{ep.episode_number}. {ep.name}</h3>
                            <div className={styles.episodeMeta}>
                                {ep.air_date && <span>{ep.air_date}</span>}
                                {ep.runtime && <span>{ep.runtime} min</span>}
                                {ep.vote_average && <span>★ {ep.vote_average.toFixed(1)}</span>}
                            </div>
                            <p>{ep.overview || "No description available."}</p>
                        </div>
                    </div>
                ))}
            </section>

            <footer className={styles.footer}>© 2025 CineBase. All rights reserved.</footer>
        </div>
    );
}