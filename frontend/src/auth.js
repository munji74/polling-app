import React, { createContext, useContext, useEffect, useState } from "react";
import { setToken, getToken } from "./api";

const AuthCtx = createContext(null);

export function AuthProvider({ children }) {
  const [token, setTok] = useState(getToken());
  const [user, setUser] = useState(null);

  useEffect(() => {
    setToken(token);
  }, [token]);

  const login = (t) => setTok(t);
  const logout = () => {
    setTok(null);
    setUser(null);
  };

  return React.createElement(
    AuthCtx.Provider,
    { value: { token, user, login, logout } },
    children
  );
}

export function useAuth() {
  return useContext(AuthCtx);
}
