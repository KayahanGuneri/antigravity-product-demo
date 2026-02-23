import React from "react";
import { Navigate } from "react-router-dom";
import useAuth from "./useAuth";

interface ProtectedRouteProps {
    children: React.ReactNode;
    allowedRoles?: string[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
    children,
    allowedRoles,
}) => {
    const { token, role } = useAuth();

    if (!token) {
        return <Navigate to="/login" replace />;
    }

    if (allowedRoles && allowedRoles.length > 0 && !allowedRoles.includes(role ?? "")) {
        return <Navigate to="/login" replace />;
    }

    return <>{children}</>;
};

export default ProtectedRoute;
