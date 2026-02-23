# Antigravity Product Demo Backend

## Prerequisites
- Java 21
- Docker & Docker Compose
- Gradle (optional, wrapper provided)

## Getting Started

1.  **Start Database**
    ```bash
    cp .env.example .env
    docker-compose up -d
    ```

2.  **Run Backend**
    ```bash
    cd backend
    # Linux/Mac
    ./gradlew bootRun
    # Windows
    ./gradlew.bat bootRun
    ```

3.  **Verify Health**
    ```bash
    curl http://localhost:8080/api/health
    # Output: {"status":"ok"}
    ```

## Frontend

### Prerequisites
- Node 18+
- npm

### Getting Started

1.  **Configure Environment**
    ```bash
    cp frontend/.env.example frontend/.env
    ```
    Ensure `VITE_API_BASE_URL` is set to your backend URL (default: `http://localhost:8080`).

2.  **Run Frontend**
    ```bash
    cd frontend
    npm install
    npm run dev
    ```
    The application will be available at `http://localhost:5173`.
