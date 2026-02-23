import React, { useState } from "react";
import type { FormEvent } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import useAuth from "../auth/useAuth";

const RegisterPage: React.FC = () => {
    const auth = useAuth();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setError(null);
        setSuccess(false);
        setLoading(true);

        try {
            await api.post("/api/auth/register", { email, password });
            setSuccess(true);
            // Redirect to /login after a short delay so the success message is visible
            setTimeout(() => {
                auth.registerSuccessRedirect();
            }, 1500);
        } catch (err: unknown) {
            if (
                err &&
                typeof err === "object" &&
                "response" in err
            ) {
                const axiosErr = err as {
                    response?: { data?: { message?: string; errors?: string[] } };
                };

                const data = axiosErr.response?.data;
                if (data?.errors && data.errors.length > 0) {
                    setError(data.errors.join(" "));
                } else {
                    setError(data?.message || "Registration failed. Please try again.");
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
                <p className="auth-subtitle">Create your account</p>

                {success && (
                    <div className="auth-success" role="status">
                        <span>✓</span> Account created! Redirecting to login…
                    </div>
                )}

                {error && (
                    <div className="auth-error" role="alert">
                        <span className="auth-error-icon">⚠</span>
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="form-group">
                        <label htmlFor="reg-email" className="form-label">
                            Email address
                        </label>
                        <input
                            id="reg-email"
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
                        <label htmlFor="reg-password" className="form-label">
                            Password
                        </label>
                        <input
                            id="reg-password"
                            type="password"
                            className="form-input"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="••••••••"
                            required
                            autoComplete="new-password"
                        />
                    </div>

                    <button
                        type="submit"
                        className="auth-btn"
                        disabled={loading || success}
                        id="register-submit-btn"
                    >
                        {loading ? (
                            <span className="btn-loading">
                                <span className="btn-spinner" />
                                Creating account…
                            </span>
                        ) : (
                            "Create Account"
                        )}
                    </button>
                </form>

                <p className="auth-switch">
                    Already have an account?{" "}
                    <Link to="/login" className="auth-link">
                        Sign in
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default RegisterPage;
