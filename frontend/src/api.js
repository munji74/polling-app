const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8080";

export function setToken(token) {
  if (token) localStorage.setItem("token", token);
  else localStorage.removeItem("token");
}

export function getToken() {
  return localStorage.getItem("token");
}

async function request(path, { method = "GET", body, auth = false } = {}) {
  const headers = { "Content-Type": "application/json" };
  if (auth) {
    const t = getToken();
    if (t) headers["Authorization"] = `Bearer ${t}`;
  }
  const res = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  if (!res.ok) {
    const msg = (data && (data.message || data.error)) || res.statusText;
    throw new Error(msg);
  }
  return data;
}

export const api = {
  signup: (username, password) =>
    request("/auth/register", { method: "POST", body: { username, password } }),
  login: (username, password) =>
    request("/auth/login", { method: "POST", body: { username, password } }),
  mePositions: () => request("/api/positions", { auth: true }),
};
