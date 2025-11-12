import MenuSheet from "./MenuSheet";

export default function Navbar({ active = "feed", onTab }) {
    return (
        <header className="navbar">
            <div className="brand">CINEBASE</div>

            <nav className="tabs">
                <button className={active === "feed" ? "active" : ""} onClick={() => onTab?.("feed")} type="button">Feed</button>
                <button className={active === "movies" ? "active" : ""} onClick={() => onTab?.("movies")} type="button">Movies</button>
                <button className={active === "tv" ? "active" : ""} onClick={() => onTab?.("tv")} type="button">TV Shows</button>
            </nav>

            <div className="user">
                <MenuSheet />
            </div>
        </header>
    );
}

