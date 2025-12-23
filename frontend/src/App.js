import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./routes/ProtectedRoute";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import MovieDetails from "./pages/MovieDetails";
import SeasonDetails from "./pages/SeasonDetails";
import ActorDetails from "./pages/ActorDetails";
import Profile from './pages/Profile';
import Favorites from './pages/Favorites';
import Watchlist from './pages/Watchlist';
import ReviewForm from './pages/ReviewForm';
import HistoryRatings from './pages/HistoryRatings';
import HigherLowerGame from './pages/HigherLowerGame';
import Leaderboard from './pages/Leaderboard';
import Onboarding from './pages/Onboarding';
import "./styles/global.css";
import "./styles/token.css";
import "./styles/layout.css";


export default function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <Routes>
                    <Route path="/" element={<Navigate to="/login" replace />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route element={<ProtectedRoute />}>
                        <Route path="/onboarding" element={<Onboarding />} />
                        <Route path="/dashboard" element={<Dashboard />} />
                        <Route path="/movie/:id" element={<MovieDetails />} />
                        <Route path="/tv/:id" element={<MovieDetails />} />
                        <Route path="/tv/:id/season/:seasonNumber" element={<SeasonDetails />} />
                        <Route path="/person/:id" element={<ActorDetails />} />
                        <Route path="/profile" element={<Profile />} />
                        <Route path="/favorites" element={<Favorites />} />
                        <Route path="/watchlist" element={<Watchlist />} />
                        <Route path="/review/:mediaType/:tmdbId" element={<ReviewForm />} />
                        <Route path="/history-ratings" element={<HistoryRatings />} />
                        <Route path="/higher-lower-game" element={<HigherLowerGame />} />
                        <Route path="/leaderboard" element={<Leaderboard />} />
                    </Route>
                    <Route path="*" element={<Navigate to="/login" replace />} />
                </Routes>
            </AuthProvider>
        </BrowserRouter>
    );
}
