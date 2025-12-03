import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { PeopleApi, imgUrl } from "../api/tmdbApi";
import Navbar from "../components/Navbar";
import styles from "./ActorDetails.module.css";

export default function ActorDetails() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function fetchActor() {
            setLoading(true);
            try {
                const response = await PeopleApi.details(id);
                setData(response);
            } catch (err) {
                console.error("Error fetching actor:", err);
            } finally {
                setLoading(false);
            }
        }
        void fetchActor();
    }, [id]);

    if (loading) return <div className={styles.loading}>Loading...</div>;
    if (!data) return <div className={styles.error}>Actor not found</div>;

    const profileImg = imgUrl(data.profile_path, "h632");

    // Combine movies and TV shows, sort by popularity
    const allCredits = [
        ...(data.movie_credits?.cast || []).map(c => ({ ...c, media_type: "movie" })),
        ...(data.tv_credits?.cast || []).map(c => ({ ...c, media_type: "tv" }))
    ].sort((a, b) => (b.vote_average || 0) - (a.vote_average || 0)).slice(0, 20);

    return (
        <div className={styles.page}>
            <Navbar active="movies" onTab={() => navigate('/dashboard')} />

            <section className={styles.hero}>

                <div className={styles.content}>
                    <div className={styles.profileWrapper}>
                        {profileImg ? (
                            <img src={profileImg} alt={data.name} className={styles.profile} />
                        ) : (
                            <div className={styles.profilePlaceholder}>?</div>
                        )}
                    </div>

                    <div className={styles.info}>
                        <h1 className={styles.name}>{data.name}</h1>

                        <div className={styles.meta}>
                            {data.known_for_department && (
                                <span><strong>Known for:</strong> {data.known_for_department}</span>
                            )}
                            {data.birthday && (
                                <span><strong>Born:</strong> {new Date(data.birthday).toLocaleDateString()}</span>
                            )}
                            {data.place_of_birth && (
                                <span><strong>Place of birth:</strong> {data.place_of_birth}</span>
                            )}
                        </div>

                        {data.biography && (
                            <div className={styles.biography}>
                                <h2>Biography</h2>
                                <p>{data.biography.length > 600
                                    ? `${data.biography.substring(0, 600)}...`
                                    : data.biography}
                                </p>
                            </div>
                        )}
                    </div>
                </div>
            </section>

            {/* Filmography */}
            <section className={styles.filmography}>
                <h2>Known For</h2>
                <div className={styles.creditsGrid}>
                    {allCredits.map((credit, idx) => {
                        const title = credit.title || credit.name;
                        const date = credit.release_date || credit.first_air_date;
                        const poster = imgUrl(credit.poster_path, "w342");

                        return (
                            <div
                                key={`${credit.id}-${idx}`}
                                className={styles.creditCard}
                                onClick={() => navigate(`/${credit.media_type}/${credit.id}`)}
                            >
                                {poster ? (
                                    <img src={poster} alt={title} className={styles.poster} />
                                ) : (
                                    <div className={styles.posterPlaceholder}>No Image</div>
                                )}
                                <div className={styles.creditInfo}>
                                    <div className={styles.creditTitle}>{title}</div>
                                    {credit.character && (
                                        <div className={styles.creditCharacter}>as {credit.character}</div>
                                    )}
                                    {date && (
                                        <div className={styles.creditYear}>
                                            {new Date(date).getFullYear()}
                                        </div>
                                    )}
                                </div>
                            </div>
                        );
                    })}
                </div>
            </section>

            <footer className={styles.footer}>© 2025 CineBase. All rights reserved.</footer>
        </div>
    );
}