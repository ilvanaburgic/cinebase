import { useNavigate } from "react-router-dom";
import MenuSheet from "./MenuSheet";

export default function Navbar({ active = "feed", onTab }) {
    const navigate = useNavigate();

    const handleTabClick = (tab) => {
        if (onTab) {
            // If onTab callback is provided, use it (Dashboard behavior)
            onTab(tab);
        } else {
            // Otherwise, navigate to Dashboard with the selected tab
            navigate(`/dashboard?scope=${tab}`);
        }
    };

    return (
        <header className="navbar">
            <div className="brand">CINEBASE</div>

            <nav className="tabs">
                <button className={active === "feed" ? "active" : ""} onClick={() => handleTabClick("feed")} type="button">Feed</button>
                <button className={active === "movies" ? "active" : ""} onClick={() => handleTabClick("movies")} type="button">Movies</button>
                <button className={active === "tv" ? "active" : ""} onClick={() => handleTabClick("tv")} type="button">TV Shows</button>
            </nav>

            <div className="user">
                <MenuSheet />
            </div>
        </header>
    );
}

