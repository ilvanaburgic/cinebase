import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { imgUrl, ConfigApi } from "../api/tmdbApi";
import api from "../api/axios";
import ConfirmationModal from "./ConfirmationModal";

/**
 * @typedef {{
 *   id:number,
 *   media_type?: 'movie'|'tv'|'person',
 *   title?:string,
 *   name?:string,
 *   release_date?:string,
 *   first_air_date?:string,
 *   genre_ids?: number[],
 *   poster_path?:string,
 *   vote_average?: number
 * }} TmdbItem
 */

/** @param {{ m?: TmdbItem }} props */
export default function MovieCard({ m = /** @type {TmdbItem} */ ({}) }) {
    const navigate = useNavigate();
    const [showSuccessModal, setShowSuccessModal] = useState(false);

    useEffect(() => { void ConfigApi.loadOnce(); }, []);

    // MEDIA TYPE
    const media = m.media_type || (m.first_air_date ? "tv" : "movie");

    // TITLE
    const title = m.title || m.name || "Unknown";

    // DATE
    const date = m.release_date || m.first_air_date || "—";

    // GENRES ARRAY
    const genreNames = (m.genre_ids || [])
        .map((id) => ConfigApi.genreName(media, id))
        .filter(Boolean)
        .join(", ");

    // POSTER
    const poster = imgUrl(m.poster_path, "w500");

    // RATING
    const rating = Number(m.vote_average || 0).toFixed(1);

    const handleClick = () => {
        navigate(`/${media}/${m.id}`);
    };

    const handleAddToFavorites = async (e) => {
        e.stopPropagation();

        try {
            await api.post("/api/favorites", {
                tmdbId: m.id,
                mediaType: media,
                title: title,
                posterPath: m.poster_path
            });

            setShowSuccessModal(true);
        } catch (err) {
            const status = err?.status ?? err?.response?.status ?? 0;

            if (status === 409) {
                alert("Already in your favorites!");
            } else if (status === 0) {
                alert("Cannot reach server. Is the backend running?");
            } else {
                alert("Failed to add to favorites");
            }
        }
    };

    return (
        <div className="movie-card" onClick={handleClick} style={{ cursor: "pointer" }}>
            <div className="thumb">
                {poster ? (
                    <img src={poster} alt={title}/>
                ) : (
                    <div style={{background: "#111", width: "100%", height: "100%"}}/>
                )}

                <div className="meta">
                    <span className="rate">★ {rating}</span>
                    <span style={{color: "#fff"}}>{media === "tv" ? "TV Show" : "Movie"}</span>
                </div>
            </div>

            <div className="info">
                <div className="title">{title}</div>
                <div className="sub">
                    {genreNames || "—"}
                    <br/>
                    {date}
                </div>
            </div>

            <div className="card-actions">
                <button
                    className="pill"
                    type="button"
                    onClick={(e) => e.stopPropagation()}
                >
                    Rate
                </button>
                <button
                    className="pill"
                    type="button"
                    onClick={handleAddToFavorites}
                >
                    ♡
                </button>
            </div>

            {/* Success Modal */}
            {showSuccessModal && (
                <ConfirmationModal
                    message={`"${title}" has been added to your favorites!`}
                    onClose={() => setShowSuccessModal(false)}
                />
            )}
        </div>
    );
}