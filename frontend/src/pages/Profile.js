import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useAuth } from "../context/AuthContext";
import api from "../api/axios";
import ConfirmationModal from "../components/ConfirmationModal";
import styles from "./Profile.module.css";

const passwordSchema = z.object({
    currentPassword: z.string().min(1, "Current password is required"),
    newPassword: z.string().min(8, "New password must be at least 8 characters"),
    confirmPassword: z.string().min(1, "Please confirm your new password"),
})
.refine((data) => data.newPassword === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
})
.refine((data) => data.currentPassword !== data.newPassword, {
    message: "New password must be different from current password",
    path: ["newPassword"],
});

export default function Profile() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [showPasswordForm, setShowPasswordForm] = useState(false);
    const [serverMsg, setServerMsg] = useState(null);
    const [successMsg, setSuccessMsg] = useState(null);
    const [showSuccessModal, setShowSuccessModal] = useState(false);

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
        reset,
        setError,
    } = useForm({
        resolver: zodResolver(passwordSchema),
        mode: "onBlur"
    });

    const onPasswordChange = async (values) => {
        setServerMsg(null);
        setSuccessMsg(null);

        try {
            await api.post("/api/auth/change-password", {
                currentPassword: values.currentPassword,
                newPassword: values.newPassword,
            });

            // Show success modal
            setShowSuccessModal(true);

            // Reset form and close password section
            reset();
            setShowPasswordForm(false);
            setSuccessMsg(null);
            setServerMsg(null);
        } catch (err) {
            const status = err?.status ?? err?.response?.status ?? 0;

            if (status === 0) {
                setServerMsg("Cannot reach server. Is the backend running on http://localhost:8080?");
                return;
            }
            if (status === 401) {
                setError("currentPassword", { type: "server", message: "Current password is incorrect" });
                return;
            }
            if (status === 400) {
                setError("newPassword", { type: "server", message: "New password must be different from current password" });
                return;
            }
            setServerMsg(err?.message ? String(err.message) : "Failed to change password");
        }
    };

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-GB', {
            day: '2-digit',
            month: 'long',
            year: 'numeric'
        });
    };

    return (
        <div className={styles.page}>
            <header className={styles.topbar}>
                <div className={styles.brand}>🎬 CINEBASE</div>
            </header>

            <main className={styles.center}>
                <div className={styles.card}>
                    <h1 className={styles.title}>Profile</h1>

                    {successMsg && <div className={`${styles.alert} ${styles.ok}`}>{successMsg}</div>}
                    {serverMsg && <div className={`${styles.alert} ${styles.warn}`}>{serverMsg}</div>}

                    {/* User Information Section */}
                    <div className={styles.infoSection}>
                        <div className={styles.infoRow}>
                            <label>Full Name</label>
                            <div className={styles.value}>{user.name} {user.surname}</div>
                        </div>

                        <div className={styles.infoRow}>
                            <label>Email</label>
                            <div className={styles.value}>{user.email}</div>
                        </div>

                        <div className={styles.infoRow}>
                            <label>Username</label>
                            <div className={styles.value}>{user.username}</div>
                        </div>

                        <div className={styles.infoRow}>
                            <label>Password</label>
                            <div className={styles.value}>••••••••</div>
                        </div>

                        <div className={styles.infoRow}>
                            <label>Member Since</label>
                            <div className={styles.value}>{formatDate(user.createdAt)}</div>
                        </div>
                    </div>

                    {/* Password Change Section */}
                    {!showPasswordForm ? (
                        <button
                            className={styles.cta}
                            onClick={() => {
                                setShowPasswordForm(true);
                                setServerMsg(null);
                                setSuccessMsg(null);
                            }}
                        >
                            Change Password
                        </button>
                    ) : (
                        <div className={styles.passwordSection}>
                            <h2 className={styles.subtitle}>Change Password</h2>

                            <form onSubmit={handleSubmit(onPasswordChange)} className={styles.form} noValidate>
                                <div className={`${styles.field} ${errors.currentPassword ? styles.err : ""}`}>
                                    <input
                                        type="password"
                                        placeholder="Current password"
                                        autoComplete="current-password"
                                        aria-invalid={!!errors.currentPassword}
                                        {...register("currentPassword")}
                                    />
                                    {errors.currentPassword && <span>{errors.currentPassword.message}</span>}
                                </div>

                                <div className={`${styles.field} ${errors.newPassword ? styles.err : ""}`}>
                                    <input
                                        type="password"
                                        placeholder="New password (min. 8 characters)"
                                        autoComplete="new-password"
                                        aria-invalid={!!errors.newPassword}
                                        {...register("newPassword")}
                                    />
                                    {errors.newPassword && <span>{errors.newPassword.message}</span>}
                                </div>

                                <div className={`${styles.field} ${errors.confirmPassword ? styles.err : ""}`}>
                                    <input
                                        type="password"
                                        placeholder="Confirm new password"
                                        autoComplete="new-password"
                                        aria-invalid={!!errors.confirmPassword}
                                        {...register("confirmPassword")}
                                    />
                                    {errors.confirmPassword && <span>{errors.confirmPassword.message}</span>}
                                </div>

                                <div className={styles.buttonGroup}>
                                    <button
                                        type="button"
                                        className={styles.cancelBtn}
                                        onClick={() => {
                                            setShowPasswordForm(false);
                                            reset();
                                            setServerMsg(null);
                                        }}
                                    >
                                        Cancel
                                    </button>
                                    <button type="submit" className={styles.cta} disabled={isSubmitting}>
                                        {isSubmitting ? "Updating..." : "Update Password"}
                                    </button>
                                </div>
                            </form>
                        </div>
                    )}

                    {/* Logout Button */}
                    <button className={styles.logoutBtn} onClick={handleLogout}>
                        Log Out
                    </button>
                </div>
            </main>

            {/* Success Modal */}
            {showSuccessModal && (
                <ConfirmationModal
                    message="Your password has been changed successfully!"
                    onClose={() => setShowSuccessModal(false)}
                />
            )}
        </div>
    );
}
