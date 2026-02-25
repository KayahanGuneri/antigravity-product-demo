import React, { createContext, useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { getToken, setToken, clearToken } from "./tokenStorage";

interface AuthUser {
    email: string;
    role: string;
}

interface AuthContextType {
    user: AuthUser | null;
    token: string | null;
    role: string | null;
    isAuthenticated: boolean;
    login: (token: string) => void;
    logout: () => void;
    registerSuccessRedirect: () => void;
}

export const AuthContext = createContext<AuthContextType>({
    user: null,
    token: null,
    role: null,
    isAuthenticated: false,
    login: () => { },
    logout: () => { },
    registerSuccessRedirect: () => { },
});

function decodeJwtPayload(token: string): Record<string, unknown> | null {
    try {
        const parts = token.split(".");
        if (parts.length !== 3) return null;
        // Base64url → Base64 → decode
        const base64 = parts[1].replace(/-/g, "+").replace(/_/g, "/");
        const padded = base64.padEnd(
            base64.length + ((4 - (base64.length % 4)) % 4),
            "=",
        );
        const jsonStr = atob(padded);
        return JSON.parse(jsonStr);
    } catch {
        return null;
    }
}

function buildUser(token: string): { user: AuthUser; role: string } | null {
    const payload = decodeJwtPayload(token);
    if (!payload) return null;

    // Spring Security JWT stores role in "role" or inside "roles" array
    const rawRole =
        (payload["role"] as string) ||
        (Array.isArray(payload["roles"])
            ? (payload["roles"] as string[])[0]
            : null) ||
        "USER";

    const email =
        (payload["sub"] as string) || (payload["email"] as string) || "";

    return {
        user: { email, role: rawRole },
        role: rawRole,
    };
}

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
    children,
}) => {
    const [token, setTokenState] = useState<string | null>(null);
    const [user, setUser] = useState<AuthUser | null>(null);
    const [role, setRole] = useState<string | null>(null);

    const navigate = useNavigate();

    // On mount: restore from tokenStorage
    useEffect(() => {
        const stored = getToken();
        if (stored) {
            const result = buildUser(stored);
            if (result) {
                setTokenState(stored);
                setUser(result.user);
                setRole(result.role);
            } else {
                // Token is invalid/malformed — clear it
                clearToken();
            }
        }
    }, []);

    const login = useCallback((newToken: string) => {
        setToken(newToken);
        const result = buildUser(newToken);
        if (result) {
            setTokenState(newToken);
            setUser(result.user);
            setRole(result.role);
        }
    }, []);

    const logout = useCallback(() => {
        clearToken();
        setTokenState(null);
        setUser(null);
        setRole(null);
        navigate("/login");
    }, [navigate]);

    const registerSuccessRedirect = useCallback(() => {
        navigate("/login");
    }, [navigate]);

    return (
        <AuthContext.Provider
            value={{
                user,
                token,
                role,
                isAuthenticated: !!token,
                login,
                logout,
                registerSuccessRedirect,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};
