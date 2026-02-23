import React from "react";
import { Outlet } from "react-router-dom";
import { AuthProvider } from "../auth/AuthContext";
import Navbar from "./Navbar";

const Layout: React.FC = () => {
  return (
    <AuthProvider>
      <div className="app-layout">
        <Navbar />
        <main className="app-main">
          <Outlet />
        </main>
        <footer className="app-footer">
          <p>© 2026 Antigravity</p>
        </footer>
      </div>
    </AuthProvider>
  );
};

export default Layout;
