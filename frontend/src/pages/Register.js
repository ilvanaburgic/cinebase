import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import api from "../api/axios";
import { useState } from "react";
import { Link } from "react-router-dom";
import styles from "./Register.module.css";

const schema = z.object({
    name: z.string().min(1, "Name is required").max(50, "Max 50 chars"),
    surname: z.string().min(1, "Surname is required").max(50, "Max 50 chars"),
    username: z
        .string()
        .min(3, "Min 3 chars")
        .max(50, "Max 50 chars")
        .regex(/^[a-z0-9._-]+$/, "Use lowercase letters, numbers, . _ - only"),
    email: z.email("Invalid email").max(120, "Max 120 chars"),
    password: z.string().min(8, "Minimum 8 characters").max(100, "Max 100 chars"),
    confirmPassword: z.string().min(1, "Please confirm your password"),
}).refine((d) => d.password === d.confirmPassword, {
    path: ["confirmPassword"],
    message: "Passwords must match",
});

export default function Register() {
    const {
        register,
        handleSubmit,
        setError,
        formState: { errors, isSubmitting },
        reset,
    } = useForm({ resolver: zodResolver(schema), mode: "onBlur" });

    const [serverMsg, setServerMsg] = useState(null);
    const [okMsg, setOkMsg] = useState(null);

    const onSubmit = async (values) => {
        setServerMsg(null);
        setOkMsg(null);

        const payload = {
            name: values.name.trim(),
            surname: values.surname.trim(),
            username: values.username.trim().toLowerCase(),
            email: values.email.trim().toLowerCase(),
            password: values.password,
        };

        try {
            const res = await api.post("/api/auth/register", payload);
            localStorage.setItem("token", res.data?.token);
            localStorage.setItem("user", JSON.stringify({
                id: res.data?.id,
                username: res.data?.username,
                email: res.data?.email,
                name: res.data?.name,
                surname: res.data?.surname,
            }));
            setOkMsg("Account created. Welcome to CineBase!");
            reset();

        } catch (err) {
        if (err?.status === 0) {
            setServerMsg("Cannot reach server. Is the backend running on http://localhost:8080?");
            return;
        }
        if (err.fields) {
            for (const [field, message] of Object.entries(err.fields)) {
                setError(field, { type: "server", message: String(message) });
            }
            setServerMsg("Please fix the highlighted fields.");
        } else {
            setServerMsg(err.message || "Registration failed.");
        }
    }

};

    return (
        <div className={styles.page}>
            <header className={styles.topbar}>
                <div className={styles.brand}>🎬 CINEBASE</div>
            </header>

            <main className={styles.center}>
                <div className={styles.card}>
                    <h1 className={styles.title}>Registration</h1>

                    {okMsg && <div className={`${styles.alert} ${styles.ok}`}>{okMsg}</div>}
                    {serverMsg && <div className={`${styles.alert} ${styles.warn}`}>{serverMsg}</div>}

                    <form onSubmit={handleSubmit(onSubmit)} className={styles.form}>
                        <div className={`${styles.field} ${errors.name ? styles.err : ""}`}>
                            <input placeholder="Name" {...register("name")} />
                            {errors.name && <span>{errors.name.message}</span>}
                        </div>

                        <div className={`${styles.field} ${errors.surname ? styles.err : ""}`}>
                            <input placeholder="Surname" {...register("surname")} />
                            {errors.surname && <span>{errors.surname.message}</span>}
                        </div>

                        <div className={`${styles.field} ${errors.username ? styles.err : ""}`}>
                            <input placeholder="Username (lowercase)" {...register("username")} />
                            {errors.username && <span>{errors.username.message}</span>}
                        </div>

                        <div className={`${styles.field} ${errors.email ? styles.err : ""}`}>
                            <input type="email" placeholder="Email" {...register("email")} />
                            {errors.email && <span>{errors.email.message}</span>}
                        </div>

                        <div className={`${styles.field} ${errors.password ? styles.err : ""}`}>
                            <input type="password" placeholder="Password" {...register("password")} />
                            {errors.password && <span>{errors.password.message}</span>}
                        </div>

                        <div className={`${styles.field} ${errors.confirmPassword ? styles.err : ""}`}>
                            <input type="password" placeholder="Confirm password" {...register("confirmPassword")} />
                            {errors.confirmPassword && <span>{errors.confirmPassword.message}</span>}
                        </div>

                        <div className={styles.muted}>
                            Already have account? <Link to="/login">Login</Link>
                        </div>

                        <button className={styles.cta} disabled={isSubmitting}>
                            {isSubmitting ? "Signing up..." : "Sign Up"}
                        </button>
                    </form>
                </div>
            </main>
        </div>
    );
}
