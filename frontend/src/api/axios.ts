import axios from "axios";
import { API_BASE_URL, IS_PROD } from "../config/env";
import { getToken, clearToken } from "../auth/tokenStorage";

export type NormalizedApiError = {
  status: number | null;
  message: string;
  details?: unknown;
};

const normalizeBaseURL = (url: string): string => {
  const trimmed = url.trim();
  if (trimmed === "/") return "/";
  if (trimmed.endsWith("/") && trimmed.length > 1) {
    return trimmed.slice(0, -1);
  }
  return trimmed;
};

const api = axios.create({
  baseURL: normalizeBaseURL(API_BASE_URL),
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

let interceptorsRegistered = false;
let isLoggingOut = false;

if (!interceptorsRegistered) {
  api.interceptors.request.use(
    (config) => {
      const token = getToken();
      if (token && token.trim() !== "") {
        // Only add token if Authorization header is not already present
        if (config.headers && !config.headers.Authorization) {
          config.headers.Authorization = `Bearer ${token}`;
        }
      }

      return config;
    },

    (error) => Promise.reject(error)
  );

  api.interceptors.response.use(
    (response) => response,
    (error) => {
      const status: number | null = error?.response?.status ?? null;

      if (status === 401) {
        clearToken();
        if (!window.location.pathname.startsWith("/login") && !isLoggingOut) {
          isLoggingOut = true;
          window.location.href = "/login";
        }
      }

      const normalizedError: NormalizedApiError = {
        status,
        message: "Something went wrong. Please try again.",
        details: error?.response?.data,
      };

      const serverMsg = error?.response?.data?.message;
      if (typeof serverMsg === "string" && serverMsg.trim() !== "") {
        normalizedError.message = serverMsg;
      } else if (status) {
        normalizedError.message = `Request failed with status ${status}`;
      } else if (!error?.response || error?.message === "Network Error") {
        normalizedError.status = null;
        normalizedError.message = "Network error. Please try again.";
      }

      if (!IS_PROD) {
        // Redacted for production safety, but kept for dev reference
        // Do not log JWT or other sensitive data here
      }

      return Promise.reject(normalizedError);
    }
  );

  interceptorsRegistered = true;
}

export default api;
