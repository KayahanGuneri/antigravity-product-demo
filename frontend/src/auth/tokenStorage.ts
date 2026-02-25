const ACCESS_TOKEN_KEY = "auth_token";

/**
 * Single source of truth for auth token persistence.
 * This file is the ONLY place allowed to touch localStorage.
 */
export const getToken = (): string | null => {
  try {
    const token = localStorage.getItem(ACCESS_TOKEN_KEY);
    return token && token.trim() !== "" ? token : null;
  } catch {
    return null;
  }
};

export const setToken = (token: string): void => {
  try {
    const value = token?.trim();
    if (!value) return;
    localStorage.setItem(ACCESS_TOKEN_KEY, value);
  } catch {
    // no-op
  }
};

export const clearToken = (): void => {
  try {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
  } catch {
    // no-op
  }
};
