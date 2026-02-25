# Antigravity Product Demo - Frontend

A Vite + React frontend for the Antigravity product demo.

## Environment Variables

The application uses environment variables for configuration. These are defined in `.env` files at the root of the frontend directory.

| Variable | Description | Default (Dev) |
| :--- | :--- | :--- |
| `VITE_API_BASE_URL` | Base URL for the backend API | `http://localhost:8080` |
| `VITE_APP_ENV` | Application environment (`development` or `production`) | `development` |

### Deployment Note

- **Same-origin deployment**: Set `VITE_API_BASE_URL=/` if the frontend and backend are served from the same domain (via a reverse proxy like Nginx).
- **Cross-origin deployment**: Set `VITE_API_BASE_URL` to the absolute URL of your backend API (e.g., `https://api.example.com`).

## Scripts

- `npm run dev`: Start the development server.
- `npm run build`: Build the application for production.
- `npm run preview`: Preview the production build locally.
- `npm run lint`: Run ESLint to check for code quality issues.
- `npm run format`: Format code using Prettier.
