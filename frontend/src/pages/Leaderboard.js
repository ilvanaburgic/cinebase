import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import api from "../api/axios";
import styles from "./Leaderboard.module.css";

export default function Leaderboard() {
    const navigate = useNavigate();
    const [leaderboard, setLeaderboard] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchLeaderboard = async () => {
            setLoading(true);
            setError(null);
            try {
                const { data } = await api.get("/api/game/higher-lower/leaderboard");
                // Defensive sort (backend should already sort, but just in case)
                const sorted = [...data].sort((a, b) => b.bestScore - a.bestScore);
                setLeaderboard(sorted);
            } catch (err) {
                console.error("Failed to load leaderboard:", err);
                setError("Failed to load leaderboard");
            } finally {
                setLoading(false);
            }
        };

        void fetchLeaderboard();
    }, []);

    return (
        <div className={styles.page}>
            <Navbar />

            <div className={styles.heroSection}>
                <div className={styles.container}>
                    <div className={styles.content}>
                        <h1 className={styles.title}>Leaderboard</h1>

                        {loading && (
                            <div className={styles.loading}>Loading leaderboard…</div>
                        )}

                        {error && (
                            <div className={styles.error}>
                                <p>{error}</p>
                                <button onClick={() => navigate("/higher-lower-game")}>
                                    Play Higher/Lower Game
                                </button>
                            </div>
                        )}

                        {!loading && !error && leaderboard.length === 0 && (
                            <div className={styles.empty}>
                                <p>No scores yet.</p>
                                <p>Play Higher/Lower to appear on the leaderboard!</p>
                                <button
                                    className={styles.playBtn}
                                    onClick={() => navigate("/higher-lower-game")}
                                >
                                    Play Now
                                </button>
                            </div>
                        )}

                        {!loading && !error && leaderboard.length > 0 && (
                            <ol className={styles.leaderboardList}>
                                {leaderboard.map((entry, index) => (
                                    <li
                                        key={entry.username}
                                        className={`${styles.leaderboardItem} ${index < 3 ? styles.topThree : ''}`}
                                    >
                                        <div className={styles.leftColumn}>
                                            <span className={styles.rank}>
                                                {index + 1}.
                                            </span>
                                            <span className={styles.username}>
                                                {entry.username}
                                            </span>
                                        </div>
                                        <div className={styles.rightColumn}>
                                            <span className={styles.score}>
                                                {entry.bestScore}
                                            </span>
                                        </div>
                                    </li>
                                ))}
                            </ol>
                        )}
                    </div>

                    {/* Decorative triangle/play shape */}
                    <div className={styles.decorShape}></div>
                </div>
            </div>
        </div>
    );
}
