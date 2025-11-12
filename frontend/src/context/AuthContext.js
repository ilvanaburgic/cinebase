import { createContext, useContext, useEffect, useMemo, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";

const AuthCtx = createContext(null);

export function AuthProvider({ children }) {
    const navigate = useNavigate();

    // Initial read from localStorage (sync)
    const [user, setUser] = useState(() => {
        try { return JSON.parse(localStorage.getItem("user") || "null"); }
        catch { return null; }
    });
    const [token, setToken] = useState(() => localStorage.getItem("token") || null);
    const [ready, setReady] = useState(false);

    useEffect(() => { setReady(true); }, []);

    const login = useCallback((u, t) => {
        setUser(u); setToken(t);
        localStorage.setItem("user", JSON.stringify(u));
        localStorage.setItem("token", t);
        navigate("/dashboard", { replace: true });
    }, [navigate]);

    const logout = useCallback(() => {
        setUser(null); setToken(null);
        localStorage.removeItem("user");
        localStorage.removeItem("token");
        navigate("/login", { replace: true });
    }, [navigate]);

    // Sync from other tabs
    useEffect(() => {
        const onStorage = () => {
            try { setUser(JSON.parse(localStorage.getItem("user") || "null")); }
            catch { setUser(null); }
            setToken(localStorage.getItem("token") || null);
        };
        window.addEventListener("storage", onStorage);
        return () => window.removeEventListener("storage", onStorage);
    }, []);

    const value = useMemo(() => ({ user, token, ready, login, logout }), [user, token, ready, login, logout]);
    return <AuthCtx.Provider value={value}>{children}</AuthCtx.Provider>;
}

export function useAuth() {
    return useContext(AuthCtx);
}
