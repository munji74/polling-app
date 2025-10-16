import React, { createContext, useContext, useEffect, useState } from "react";
import { setToken, getToken } from "./api";

const AuthCtx = createContext();

export function AuthProvider({ children }) {
  const [token, setTok] = useState(getToken());
  const [user, setUser] = useState(null); // we can derive from JWT later if needed

  useEffect(() => {
    setToken(token);
  }, [token]);

  const login = (t) => setTok(t);
  const logout = () => { setTok(null); setUser(null); };

  return (
    <AuthCtx.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthCtx.Provider>
  );
}

export function useAuth() {
  return useContext(AuthCtx);
}
