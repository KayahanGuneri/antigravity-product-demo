import React from "react";
import { Outlet } from "react-router-dom";

const Layout: React.FC = () => {
  return (
    <div className="app-layout">
      <header>
        <nav>{/* Nav items will go here */}</nav>
      </header>
      <main>
        <Outlet />
      </main>
      <footer>
        <p>&copy; 2026 Antigravity</p>
      </footer>
    </div>
  );
};

export default Layout;
