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
