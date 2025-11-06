// App.jsx
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import "./styles/global.css"; // <— global, ne module

function PrivateRoute({ children }) {
    const token = localStorage.getItem("token");
    return token ? children : <Navigate to="/login" replace />;
}

function Dashboard() {
    const user = (() => {
        try {
            return JSON.parse(localStorage.getItem("user") || "{}");
        } catch {
            return {};
        }
    })();

    const handleLogout = () => {
        localStorage.clear();
        // u SPA je bolje Navigate hook, ali ovdje je najkraće rješenje:
        window.history.replaceState(null, "", "/login");
        // ili još bolje:
        // const nav = useNavigate(); nav("/login", { replace: true });
        window.location.reload();
    };

    return (
        <div className="page">
            <header className="topbar">
                <div className="brand">🎬 CINEBASE</div>
            </header>

            <main className="center">
                <div className="card">
                    <h1 className="title">Dashboard</h1>
                    <p>
                        Welcome, <b>{user?.name || user?.username || "User"}</b>!
                    </p>
                    <button className="cta" onClick={handleLogout}>
                        Logout
                    </button>
                </div>
            </main>
        </div>
    );
}

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Navigate to="/login" replace />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route
                    path="/dashboard"
                    element={
                        <PrivateRoute>
                            <Dashboard />
                        </PrivateRoute>
                    }
                />
                <Route path="*" element={<Navigate to="/login" replace />} />
            </Routes>
        </BrowserRouter>
    );
}
