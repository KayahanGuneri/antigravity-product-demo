import { assert } from "../utils/assert";

export function getApiBaseUrl(): string {
    const url = import.meta.env.VITE_API_BASE_URL;
    assert(url, "VITE_API_BASE_URL is missing or empty");
    return url;
}
