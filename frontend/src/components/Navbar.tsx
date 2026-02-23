import React from "react";
import { Link } from "react-router-dom";
import useAuth from "../auth/useAuth";

const Navbar: React.FC = () => {
    const { isAuthenticated, user, role, logout } = useAuth();

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <Link to="/" className="navbar-logo">
                    <span className="navbar-logo-icon">⚡</span>
                    Antigravity
                </Link>
            </div>

            <div className="navbar-actions">
                {isAuthenticated ? (
                    <>
                        <div className="navbar-user-info">
                            <span className="user-email">{user?.email}</span>
                            <span className={`user-role role-${role?.toLowerCase()}`}>
                                {role}
                            </span>
                        </div>
                        <button
                            onClick={logout}
                            className="navbar-btn navbar-btn-logout"
                            id="logout-btn"
                        >
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link to="/login" className="navbar-btn navbar-btn-ghost" id="nav-login-link">
                            Sign In
                        </Link>
                        <Link to="/register" className="navbar-btn navbar-btn-primary" id="nav-register-link">
                            Register
                        </Link>
                    </>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
