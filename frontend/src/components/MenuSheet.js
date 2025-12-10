import { useEffect, useRef, useState } from "react";
import { useAuth } from "../context/AuthContext";

function Item({ href, onClick, icon, children, disabled }) {
    const Tag = href && !disabled ? "a" : "button";
    const handle = (e) => {
        if (disabled || !href) e.preventDefault();
        if (onClick) onClick(e);
    };
    return (
        <Tag
            href={href || "#"}
            onClick={handle}
            className={`menu-item${disabled ? " is-disabled": ""}`}
            {...(Tag === "button" ? { type: "button" } : {})}
        >
            <span className="mi-icon" aria-hidden>{icon}</span>
            <span className="mi-text">{children}</span>
        </Tag>
    );

}


const IcUser   = <svg width="18" height="18" viewBox="0 0 24 24"><path fill="currentColor" d="M12 12a5 5 0 1 0-5-5a5 5 0 0 0 5 5m0 2c-4.418 0-8 2.239-8 5v1h16v-1c0-2.761-3.582-5-8-5"/></svg>;
const IcBm     = <svg width="18" height="18" viewBox="0 0 24 24"><path fill="currentColor" d="M6 2h12a1 1 0 0 1 1 1v18l-7-3l-7 3V3a1 1 0 0 1 1-1"/></svg>;
const IcHeart  = <svg width="18" height="18" viewBox="0 0 24 24"><path fill="currentColor" d="M12.1 21.35L10 19.28C5.4 15.36 2 12.28 2 8.5A4.5 4.5 0 0 1 6.5 4c1.74 0 3.41.81 4.5 2.09A6 6 0 0 1 20 8.5c0 3.78-3.4 6.86-8 10.78z"/></svg>;
const IcStar   = <svg width="18" height="18" viewBox="0 0 24 24"><path fill="currentColor" d="m12 17.27l6.18 3.73l-1.64-7.03L22 9.24l-7.19-.61L12 2L9.19 8.63L2 9.24l5.46 4.73L5.82 21z"/></svg>;
const IcGame   = <svg width="18" height="18" viewBox="0 0 24 24"><path fill="currentColor" d="M3 6h18l-2 12H5L3 6m7 2H5.8l1.3 8H10V8m9 0h-4v8h2.9L19 8Z"/></svg>;
const IcLead   = <svg width="18" height="18" viewBox="0 0 24 24"><path fill="currentColor" d="M7 21v-8H3V3h8v10H9v8H7m6 0v-5h-2V3h10v13h-2v5h-2v-5h-2v5h-2Z"/></svg>;
const IcLogout = <svg width="18" height="18" viewBox="0 0 24 24"><path fill="currentColor" d="M14 7v-2H3v14h11v-2H5V7h9m3 0l5 5l-5 5v-3H9v-4h8V7Z"/></svg>;

export default function MenuSheet() {
    const { logout } = useAuth();
    const [open, setOpen] = useState(false);
    const btnRef = useRef(null);
    const sheetRef = useRef(null);

    useEffect(() => {
        const onClick = (e) => {
            if (!open) return;
            if (sheetRef.current && !sheetRef.current.contains(e.target) &&
                btnRef.current && !btnRef.current.contains(e.target)) {
                setOpen(false);
            }
        };
        const onKey = (e) => e.key === "Escape" && setOpen(false);
        document.addEventListener("mousedown", onClick);
        document.addEventListener("keydown", onKey);
        return () => {
            document.removeEventListener("mousedown", onClick);
            document.removeEventListener("keydown", onKey);
        };
    }, [open]);

    return (
        <div className="menu-wrap">
            <button
                ref={btnRef}
                className="icon-btn menu-btn"
                aria-haspopup="menu"
                aria-expanded={open}
                onClick={() => setOpen(v => !v)}
            >
                <span className="menu-hamburger" aria-hidden><span></span><span></span><span></span></span>
            </button>

            {open && (
                <div ref={sheetRef} className="menu-sheet" role="menu">
                    <Item icon={IcUser} href="/profile">Profile</Item>
                    <Item icon={IcBm}    disabled>Watchlist</Item>
                    <Item icon={IcHeart} href="/favorites">Favorites</Item>
                    <Item icon={IcStar}  disabled>Ratings</Item>
                    <Item icon={IcGame}  disabled>Higher / Lower game</Item>
                    <Item icon={IcLead}  disabled>Leaderboard</Item>
                    <hr className="menu-sep" />
                    <Item icon={IcLogout} onClick={logout}>Log out</Item>
                </div>
            )}
        </div>
    );
}
