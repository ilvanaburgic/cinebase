import { useEffect, useMemo, useState } from "react";
import Navbar from "../components/Navbar";
import SearchBar from "../components/SearchBar";
import Filters from "../components/Filters";
import MovieCard from "../components/MovieCard";
import SkeletonCard from "../components/SkeletonCard";
import { ConfigApi, FeedApi, MoviesApi, TvApi, MultiApi } from "../api/tmdbApi";

/**
 * @typedef {Object} TMDBPagedResponse
 * @property {Array} results
 * @property {number} total_results
 * @property {number} total_pages
 * @property {number} page
 */

export default function Dashboard() {
    const [scope, setScope] = useState("feed");
    const [tab, setTab] = useState("popular");
    const [q, setQ] = useState("");
    const [page, setPage] = useState(1);
    /** @type {[TMDBPagedResponse | null, Function]} */
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");

    useEffect(() => {
        void ConfigApi.loadOnce();
    }, []);

    const title = useMemo(() => {
        if (q) return `Results for "${q}"`;
        return tab === "popular" ? "Most popular" : tab === "latest" ? "Latest" : "Highest rate";
    }, [q, tab]);

    useEffect(() => {
        let cancelled = false;

        async function load() {
            setLoading(true);
            setErr("");

            try {
                let apiCall;

                // Search
                if (q) {
                    if (scope === "feed") apiCall = () => MultiApi.search(q, page);
                    else if (scope === "movies") apiCall = () => MoviesApi.search(q, page);
                    else apiCall = () => TvApi.search(q, page);
                }
                // Feed
                else if (scope === "feed") {
                    if (tab === "popular") apiCall = () => FeedApi.popular(page);
                    else if (tab === "latest") apiCall = () => FeedApi.latest(page);
                    else apiCall = () => FeedApi.topRated(page);
                }
                // Movies
                else if (scope === "movies") {
                    if (tab === "popular") apiCall = () => MoviesApi.popular(page);
                    else if (tab === "latest") apiCall = () => MoviesApi.latest(page);
                    else apiCall = () => MoviesApi.topRated(page);
                }
                // TV
                else {
                    if (tab === "popular") apiCall = () => TvApi.popular(page);
                    else if (tab === "latest") apiCall = () => TvApi.latest(page);
                    else apiCall = () => TvApi.topRated(page);
                }

                const res = await apiCall();

                if (!cancelled) {
                    // Filter out "person" results from multi search
                    const results = (res?.results || []).filter(
                        (x) => (x.media_type ?? (scope === "movies" ? "movie" : scope === "tv" ? "tv" : "movie")) !== "person"
                    );
                    setData({ ...res, results });
                }
            } catch (e) {
                if (!cancelled) {
                    const msg = e?.response?.data?.message || e?.message || "Failed to load";
                    setErr(msg);
                    setData(null);
                }
            } finally {
                if (!cancelled) setLoading(false);
            }
        }

        void load();
        return () => { cancelled = true; };
    }, [scope, tab, q, page]);

    const onSearchSubmit = (e) => {
        e.preventDefault();
        setPage(1);
    };

    const handleScope = (s) => {
        setScope(s);
        setTab("popular");
        setQ("");
        setPage(1);
    };

    return (
        <div className="dashboard">
            <Navbar active={scope} onTab={handleScope} />

            <section className="hero">
                <h1>Welcome!</h1>
                <p>Millions of movies, TV shows and people to discover. Explore now.</p>
                <SearchBar value={q} onChange={setQ} onSubmit={onSearchSubmit} />
            </section>

            <div className="container">
                <Filters
                    active={q ? "" : tab}
                    onChange={(key) => { setQ(""); setTab(key); setPage(1); }}
                />

                <div className="section-head">
                    <h2 className="section-title">{title}</h2>
                    {!!data?.total_results && (
                        <span className="count">{data.total_results.toLocaleString()} results</span>
                    )}
                </div>

                {err && <div className="alert warn" style={{ marginBottom: 12 }}>{err}</div>}

                <div className="grid">
                    {loading && Array.from({ length: 12 }).map((_, i) => <SkeletonCard key={i} />)}
                    {!loading && data?.results?.map((m) => (
                        <MovieCard
                            key={`${m.media_type || (scope === "movies" ? "movie" : "tv")}-${m.id}`}
                            m={m}
                        />
                    ))}
                </div>

                <div className="pagination">
                    <button
                        disabled={page <= 1 || loading}
                        onClick={() => setPage(p => p - 1)}
                        type="button"
                    >
                        Prev
                    </button>
                    <span>{page} / {data?.total_pages || "-"}</span>
                    <button
                        disabled={Boolean(loading || (data && page >= (data.total_pages || 0)))}
                        onClick={() => setPage(p => p + 1)}
                        type="button"
                    >
                        Next
                    </button>
                </div>
            </div>

            <footer className="footer">© 2025 CineBase. All rights reserved.</footer>
        </div>
    );
}