import axios from "axios";
import { getApiBaseUrl } from "../config/env";

const api = axios.create({
  baseURL: getApiBaseUrl(),
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;
