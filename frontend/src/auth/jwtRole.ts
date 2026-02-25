import { jwtDecode } from "jwt-decode";

export const getJwtRole = (token: string | null): "USER" | "ADMIN" | null => {
    if (!token) return null;

    try {
        const payload = jwtDecode<{ role?: string; roles?: string[] }>(token);

        // Support both "role" claim and "roles" array from Spring Security
        const role = payload.role || (Array.isArray(payload.roles) ? payload.roles[0] : null);

        if (role === "ADMIN") return "ADMIN";
        if (role === "USER") return "USER";

        return null;
    } catch (error) {
        return null;
    }
};
