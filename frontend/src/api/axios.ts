import axios from "axios";
import { getApiBaseUrl } from "../config/env";
import { getToken, clearToken } from "../auth/tokenStorage";

export type NormalizedApiError = {
  status: number | null;
  message: string;
  details?: unknown;
};

const api = axios.create({
  baseURL: getApiBaseUrl(),
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
      const headers = ((config.headers ??= {}) as any);

      // Respect per-request Authorization header if already set
      const existingAuth = headers.Authorization;
      if (typeof existingAuth === "string" && existingAuth.trim() !== "") {
        return config;
      }

      const token = getToken();
      if (token && token.trim() !== "") {
        headers.Authorization = `Bearer ${token}`;
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

      return Promise.reject(normalizedError);
    }
  );

  interceptorsRegistered = true;
}

export default api;
