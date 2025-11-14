import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import MovieDetails from "./pages/MovieDetails";
import SeasonDetails from "./pages/SeasonDetails";
import ActorDetails from "./pages/ActorDetails";
import "./styles/global.css";
import "./styles/token.css";
import "./styles/layout.css";
import ProtectedRoute from "./routes/ProtectedRoute";

export default function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <Routes>
                    <Route path="/" element={<Navigate to="/login" replace />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route element={<ProtectedRoute />}>
                        <Route path="/dashboard" element={<Dashboard />} />
                        <Route path="/movie/:id" element={<MovieDetails />} />
                        <Route path="/tv/:id" element={<MovieDetails />} />
                        <Route path="/tv/:id/season/:seasonNumber" element={<SeasonDetails />} />
                        <Route path="/person/:id" element={<ActorDetails />} />
                    </Route>
                    <Route path="*" element={<Navigate to="/login" replace />} />
                </Routes>
            </AuthProvider>
        </BrowserRouter>
    );
}
