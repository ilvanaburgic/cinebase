import { useEffect } from "react";
import { imgUrl, ConfigApi } from "../api/tmdbApi";

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

    return (
        <div className="movie-card">
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
                <button className="pill" type="button">Rate</button>
                <button className="pill" type="button">♡</button>
            </div>
        </div>
    );
}
