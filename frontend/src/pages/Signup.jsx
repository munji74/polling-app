import React, { useState } from "react";
import { api } from "../api";
import { Link, useNavigate } from "react-router-dom";

export default function Signup() {
  const [username, setU] = useState("");
  const [password, setP] = useState("");
  const [err, setErr] = useState("");
  const [ok, setOk] = useState(false);
  const nav = useNavigate();

  async function submit(e) {
    e.preventDefault();
    setErr(""); setOk(false);
    try {
      await api.signup(username, password);
      setOk(true);
      setTimeout(() => nav("/login"), 800);
    } catch (e2) {
      setErr(e2.message || "Failed to sign up");
    }
  }

  return (
    <div style={{maxWidth: 420, margin: "40px auto", fontFamily: "system-ui"}}>
      <h2>Create account</h2>
      <form onSubmit={submit}>
        <label>Username</label>
        <input value={username} onChange={e=>setU(e.target.value)} required
               style={{display:"block", width:"100%", marginBottom:8}} />
        <label>Password</label>
        <input type="password" value={password} onChange={e=>setP(e.target.value)} required
               style={{display:"block", width:"100%", marginBottom:12}} />
        <button type="submit">Sign up</button>
      </form>
      {ok && <p style={{color:"green"}}>Registered! Redirecting to login…</p>}
      {err && <p style={{color:"crimson"}}>{err}</p>}
      <p style={{marginTop:12}}>Already have an account? <Link to="/login">Log in</Link></p>
    </div>
  );
}
