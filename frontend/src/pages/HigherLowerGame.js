import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import api from "../api/axios";
import { imgUrl, ConfigApi } from "../api/tmdbApi";
import styles from "./HigherLowerGame.module.css";

export default function HigherLowerGame() {
    const navigate = useNavigate();
    const [questions, setQuestions] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [score, setScore] = useState(0);
    const [showAnswer, setShowAnswer] = useState(false);
    const [gameOver, setGameOver] = useState(false);
    const [timer, setTimer] = useState(60);
    const [loading, setLoading] = useState(true);
    const [isCorrect, setIsCorrect] = useState(null);

    const loadQuestions = useCallback(async () => {
        setLoading(true);
        try {
            const { data } = await api.get("/api/game/higher-lower/questions?count=10");
            setQuestions(data);
        } catch (err) {
            setQuestions([]);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        void ConfigApi.loadOnce();
        void loadQuestions();
    }, [loadQuestions]);

    // Submit score when game ends
    useEffect(() => {
        const submitScore = async () => {
            if (!gameOver || score === 0) return;
            try {
                await api.post("/api/game/higher-lower/submit-score", { score });
                console.log("Score submitted:", score);
            } catch (err) {
                console.error("Failed to submit score:", err);
            }
        };

        void submitScore();
    }, [gameOver, score]);

    useEffect(() => {
        if (loading || gameOver || showAnswer) return;

        const interval = setInterval(() => {
            setTimer(prev => {
                if (prev <= 1) {
                    setGameOver(true);
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);

        return () => clearInterval(interval);
    }, [loading, gameOver, showAnswer]);

    const handleAnswer = (guess) => {
        const totalQuestions = questions.length / 2;
        if (currentIndex >= totalQuestions - 1) {
            setGameOver(true);
            return;
        }

        // Use paired indexing: each question uses 2 consecutive items
        const leftItem = questions[currentIndex * 2];
        const rightItem = questions[currentIndex * 2 + 1];

        // Both items should have the same metric now
        const correct = (guess === "higher" && rightItem?.value > leftItem?.value) ||
                       (guess === "lower" && rightItem?.value < leftItem?.value);

        setIsCorrect(correct);
        if (correct) {
            setScore(prevScore => prevScore + 1);
        }

        setShowAnswer(true);

        setTimeout(() => {
            setShowAnswer(false);
            setIsCorrect(null);
            setCurrentIndex(prevIndex => {
                if (prevIndex >= totalQuestions - 1) {
                    setGameOver(true);
                    return prevIndex;
                }
                return prevIndex + 1;
            });
        }, 2000);
    };

    const handleRestart = () => {
        setCurrentIndex(0);
        setScore(0);
        setTimer(60);
        setGameOver(false);
        setShowAnswer(false);
        setIsCorrect(null);
        void loadQuestions();
    };

    const getFeedback = (finalScore, total) => {
        if (finalScore === total) return "⭐ Mastermind! Nevjerovatno!";
        if (finalScore === total - 1) return "🔥 Almost perfect!";
        if (finalScore === total - 2) return "💪 Great job!";
        if (finalScore === total - 3) return "👍 Solid performance!";
        if (finalScore === total - 4) return "🙂 Not bad, keep going!";
        if (finalScore === total - 5) return "😐 Okay-ish…";
        if (finalScore === total - 6) return "😬 You can do better!";
        return "❌ Try again!";
    };

    if (loading) {
        return (
            <div className={styles.page}>
                <Navbar />
                <div className={styles.loading}>Loading game...</div>
            </div>
        );
    }

    if (questions.length < 2) {
        return (
            <div className={styles.page}>
                <Navbar />
                <div className={styles.error}>
                    <p>Not enough questions available</p>
                    <button onClick={() => navigate("/dashboard")}>Go to Dashboard</button>
                </div>
            </div>
        );
    }

    // Use paired indexing: each question uses 2 items
    const totalQuestions = questions.length / 2;
    const leftCard = questions[currentIndex * 2];
    const rightCard = questions[currentIndex * 2 + 1];

    if (gameOver) {
        return (
            <div className={styles.page}>
                <Navbar />
                <div className={styles.gameOverContainer}>
                    <h1 className={styles.gameOverTitle}>Game Over!</h1>
                    <div className={styles.finalScore}>
                        Your Score: <span>{score}</span> / {totalQuestions}
                    </div>
                    <div className={styles.feedback}>
                        {getFeedback(score, totalQuestions)}
                    </div>
                    <div className={styles.actions}>
                        <button className={styles.restartBtn} onClick={handleRestart}>
                            Play Again
                        </button>
                        <button className={styles.dashboardBtn} onClick={() => navigate("/dashboard")}>
                            Dashboard
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.page}>
            <Navbar />

            <div className={styles.gameContainer}>
                {/* Header */}
                <div className={styles.header}>
                    <div className={styles.timer}>⏱ {timer}s</div>
                    <div className={styles.progress}>
                        {currentIndex + 1} / {totalQuestions}
                    </div>
                    <div className={styles.scoreDisplay}>Score: {score}</div>
                </div>

                {/* Question */}
                <div className={styles.questionBox}>
                    <h2 className={styles.question}>
                        Which has <span className={styles.highlight}>MORE</span>
                        <br />
                        <span className={styles.metric}>{leftCard?.metric}</span>?
                    </h2>
                </div>

                {/* Cards */}
                <div className={styles.cardsContainer}>
                    {/* Left Card - Current (with value shown) */}
                    <div className={styles.card}>
                        <div className={styles.posterBox}>
                            {leftCard?.posterPath ? (
                                <img
                                    src={imgUrl(leftCard.posterPath, "w342")}
                                    alt={leftCard?.title}
                                    className={styles.poster}
                                />
                            ) : (
                                <div className={styles.noPoster}>No Image</div>
                            )}
                        </div>
                        <div className={styles.cardTitle}>{leftCard?.title}</div>
                        <div className={styles.valueBox}>
                            <div className={styles.valueLabel}>{leftCard?.metric}</div>
                            <div className={styles.valueNumber}>{leftCard?.value}</div>
                        </div>
                    </div>

                    {/* Right Card - Next (value hidden/shown) */}
                    <div className={`${styles.card} ${showAnswer ? styles.answered : ""} ${isCorrect === true ? styles.correct : ""} ${isCorrect === false ? styles.incorrect : ""}`}>
                        <div className={styles.posterBox}>
                            {rightCard?.posterPath ? (
                                <img
                                    src={imgUrl(rightCard.posterPath, "w342")}
                                    alt={rightCard?.title}
                                    className={styles.poster}
                                />
                            ) : (
                                <div className={styles.noPoster}>No Image</div>
                            )}
                        </div>
                        <div className={styles.cardTitle}>{rightCard?.title}</div>
                        <div className={styles.valueBox}>
                            <div className={styles.valueLabel}>{rightCard?.metric}</div>
                            {showAnswer ? (
                                <div className={styles.valueNumber}>{rightCard?.value}</div>
                            ) : (
                                <div className={styles.hiddenValue}>?</div>
                            )}
                        </div>
                    </div>

                    {/* Score Badge - overlay */}
                    <div className={styles.scoreBadge}>
                        <div className={styles.label}>Score</div>
                        <div className={styles.value}>{score}</div>
                    </div>
                </div>

                {/* Buttons */}
                {!showAnswer && (
                    <div className={styles.buttonsContainer}>
                        <button
                            className={styles.higherBtn}
                            onClick={() => handleAnswer("higher")}
                            disabled={showAnswer}
                        >
                            HIGHER ↑
                        </button>
                        <button
                            className={styles.lowerBtn}
                            onClick={() => handleAnswer("lower")}
                            disabled={showAnswer}
                        >
                            LOWER ↓
                        </button>
                    </div>
                )}

                {showAnswer && (
                    <div className={styles.resultMessage}>
                        {isCorrect ? (
                            <span className={styles.correctMsg}>✓ Correct!</span>
                        ) : (
                            <span className={styles.incorrectMsg}>✗ Wrong!</span>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}
