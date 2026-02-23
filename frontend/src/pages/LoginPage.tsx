import React, { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axios";
import useAuth from "../auth/useAuth";

const LoginPage: React.FC = () => {
    const auth = useAuth();
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            const response = await api.post<{
                accessToken: string;
                tokenType: string;
                expiresInSeconds: number;
                role: string;
            }>("/api/auth/login", {
                email,
                password,
            });

            const { accessToken } = response.data;
            if (!accessToken) {
                setError("Login failed: invalid token response");
                return;
            }
            auth.login(accessToken);
            navigate("/");
        } catch (err: unknown) {
            if (
                err &&
                typeof err === "object" &&
                "response" in err
            ) {
                const axiosErr = err as { response?: { status?: number; data?: { message?: string } } };
                if (axiosErr.response?.status === 401) {
                    setError("Invalid credentials. Please check your email and password.");
                } else {
                    setError(
                        axiosErr.response?.data?.message || "Login failed. Please try again.",
                    );
                }
            } else {
                setError("Network error. Please try again.");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-card">
                <div className="auth-logo">
                    <span className="auth-logo-icon">⚡</span>
                    <h1 className="auth-title">Antigravity</h1>
                </div>
                <p className="auth-subtitle">Sign in to your account</p>

                {error && (
                    <div className="auth-error" role="alert">
                        <span className="auth-error-icon">⚠</span>
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="form-group">
                        <label htmlFor="email" className="form-label">
                            Email address
                        </label>
                        <input
                            id="email"
                            type="email"
                            className="form-input"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="you@example.com"
                            required
                            autoComplete="email"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password" className="form-label">
                            Password
                        </label>
                        <input
                            id="password"
                            type="password"
                            className="form-input"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="••••••••"
                            required
                            autoComplete="current-password"
                        />
                    </div>

                    <button
                        type="submit"
                        className="auth-btn"
                        disabled={loading}
                        id="login-submit-btn"
                    >
                        {loading ? (
                            <span className="btn-loading">
                                <span className="btn-spinner" />
                                Signing in…
                            </span>
                        ) : (
                            "Sign In"
                        )}
                    </button>
                </form>

                <p className="auth-switch">
                    Don't have an account?{" "}
                    <Link to="/register" className="auth-link">
                        Register
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default LoginPage;
