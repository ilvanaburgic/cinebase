import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080",
    headers: { "Content-Type": "application/json" }
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
});

api.interceptors.response.use(
    (res) => res,
    (error) => {
        const data = error?.response?.data || {};
        const status = error?.response?.status ?? 0;
        const url = error?.config?.url || "";

        // Don't auto-logout on 401 for change-password endpoint
        // (401 means wrong current password, not invalid token)
        if (status === 401 && !url.includes("/change-password")) {
            localStorage.removeItem("token");
            if (window.location.pathname !== "/login") {
                window.location.replace("/login");
            }
        }

        const fields = data?.fields && typeof data.fields === "object"
            ? Object.fromEntries(Object.entries(data.fields).map(([k, v]) => [k, String(v)]))
            : null;

        return Promise.reject({
            status,
            message: typeof data?.message === "string" ? data.message : "Server error",
            fields
        });
    }
);

export default api;
