

const config = Object.freeze({
  API_BASE_URL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
  APP_ENV: import.meta.env.VITE_APP_ENV || "development",
  IS_PROD: import.meta.env.VITE_APP_ENV === "production" || import.meta.env.PROD === true,
});

export const { API_BASE_URL, APP_ENV, IS_PROD } = config;

export function getApiBaseUrl(): string {
  return API_BASE_URL;
}

