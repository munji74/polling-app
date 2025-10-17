import React, { useState } from "react";
import { api } from "../api";
import { useAuth } from "../auth";
import { Link, useNavigate } from "react-router-dom";

export default function Login() {
  const [username, setU] = useState("");
  const [password, setP] = useState("");
  const [err, setErr] = useState("");
  const nav = useNavigate();
  const { login } = useAuth();

  async function submit(e) {
    e.preventDefault();
    setErr("");
    try {
      const res = await api.login(username, password);
      // { accessToken, expiresInSeconds }
      login(res.accessToken);
      nav("/dashboard");
    } catch (e2) {
      setErr(e2.message || "Invalid credentials");
    }
  }

  return (
    <div style={{maxWidth: 420, margin: "40px auto", fontFamily: "system-ui"}}>
      <h2>Log in</h2>
      <form onSubmit={submit}>
        <label>Username</label>
        <input value={username} onChange={e=>setU(e.target.value)} required
               style={{display:"block", width:"100%", marginBottom:8}} />
        <label>Password</label>
        <input type="password" value={password} onChange={e=>setP(e.target.value)} required
               style={{display:"block", width:"100%", marginBottom:12}} />
        <button type="submit">Login</button>
      </form>
      {err && <p style={{color:"crimson"}}>{err}</p>}
      <p style={{marginTop:12}}>No account? <Link to="/signup">Sign up</Link></p>
    </div>
  );
}
