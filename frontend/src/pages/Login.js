import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import api from "../api/axios";
import styles from "./Login.module.css";
import { useAuth } from "../context/AuthContext";


const schema = z.object({
    identifier: z.string().min(1, "Email or username is required"),
    password: z.string().min(1, "Password is required"),
});

export default function Login() {
    const {login} = useAuth();
    const {
        register,
        handleSubmit,
        formState: {errors, isSubmitting},
        setError,
    } = useForm({resolver: zodResolver(schema), mode: "onBlur"});

    const [serverMsg, setServerMsg] = useState(null);
    const [wakingUp, setWakingUp] = useState(false);

    // Wake up the backend server on component mount
    useEffect(() => {
        const startTime = Date.now();
        setWakingUp(true);

        api.get("/api/health")
            .catch(() => {
                // Silently fail - server will wake up anyway
            })
            .finally(() => {
                const elapsed = Date.now() - startTime;
                // Keep "waking up" message for at least 2 seconds if server was slow
                if (elapsed > 2000) {
                    setTimeout(() => setWakingUp(false), 500);
                } else {
                    setWakingUp(false);
                }
            });
    }, []);

    const onSubmit = async (values) => {
        setServerMsg(null);
        try {
            const {data} = await api.post("/api/auth/login", values);

            localStorage.setItem("token", data.token);
            const userObj = {
                id: data.id,
                username: data.username,
                email: data.email,
                name: data.name,
                surname: data.surname,
                createdAt: data.createdAt,
            };

            // Check if user has completed onboarding
            try {
                const { data: hasCompleted } = await api.get("/api/preferences/has-completed-onboarding");
                login(userObj, data.token);
                // Note: login() will navigate, but we'll override if needed
                if (!hasCompleted) {
                    window.location.href = "/onboarding";
                }
            } catch (err) {
                // If check fails, proceed to dashboard anyway
                login(userObj, data.token);
            }

        }
    catch
    (err)
    {
        const status = err?.status ?? err?.response?.status ?? 0;

        if (status === 0) {
            setServerMsg("Cannot reach server. Is the backend running on http://localhost:8080?");
            return;
        }
        if (status === 401) {
            setError("identifier", {type: "server", message: "Invalid credentials"});
            setError("password", {type: "server", message: "Invalid credentials"});
            return;
        }
        if (err && typeof err === "object" && err.fields && typeof err.fields === "object") {
            for (const [field, msg] of Object.entries(err.fields)) {
                if (field === "identifier" || field === "password") {
                    setError(field, {type: "server", message: String(msg)});
                }
            }
            setServerMsg("Please correct the highlighted fields.");
            return;
        }
        setServerMsg(err?.message ? String(err.message) : "Unexpected error");
    }
};

    return (
        <div className={styles.page}>
            <header className={styles.topbar}>
                <div className={styles.brand}>🎬 CINEBASE</div>
            </header>

            <main className={styles.center}>
                <div className={styles.card}>
                    <h1 className={styles.title}>Login</h1>

                    {wakingUp && (
                        <div className={`${styles.alert} ${styles.info}`}>
                            ⏳ Waking up the server... This may take up to 60 seconds on first visit.
                        </div>
                    )}

                    {serverMsg && <div className={`${styles.alert} ${styles.warn}`}>{serverMsg}</div>}

                    <form onSubmit={handleSubmit(onSubmit)} className={styles.form} noValidate>
                        <div className={`${styles.field} ${errors.identifier ? styles.err : ""}`}>
                            <input
                                placeholder="Email or username"
                                autoComplete="username"
                                aria-invalid={!!errors.identifier}
                                {...register("identifier")}
                            />
                            {errors.identifier && <span>{errors.identifier.message}</span>}
                        </div>

                        <div className={`${styles.field} ${errors.password ? styles.err : ""}`}>
                            <input
                                type="password"
                                placeholder="Password"
                                autoComplete="current-password"
                                aria-invalid={!!errors.password}
                                {...register("password")}
                            />
                            {errors.password && <span>{errors.password.message}</span>}
                        </div>

                        <div className={styles.muted}>
                            Don’t have an account? <Link to="/register">Register</Link>
                        </div>

                        <button className={styles.cta} disabled={isSubmitting}>
                            {isSubmitting ? "Signing in..." : "Login"}
                        </button>
                    </form>
                </div>
            </main>
        </div>
    );
}
