import React, { useEffect, useState } from "react";
import { api } from "../api";
import { useAuth } from "../auth";

export default function Dashboard() {
  const { logout } = useAuth();
  const [positions, setPositions] = useState([]);
  const [err, setErr] = useState("");

  useEffect(() => {
    (async () => {
      try {
        const data = await api.mePositions();
        setPositions(data);
      } catch (e) {
        setErr(e.message || "Failed to load");
      }
    })();
  }, []);

  return (
    <div style={{maxWidth: 720, margin: "40px auto", fontFamily: "system-ui"}}>
      <div style={{display:"flex", justifyContent:"space-between", alignItems:"center"}}>
        <h2>Dashboard</h2>
        <button onClick={logout}>Logout</button>
      </div>
      {err && <p style={{color:"crimson"}}>{err}</p>}
      <ul>
        {positions.map(p => <li key={p.id}>{p.name}</li>)}
      </ul>
      <p style={{opacity:.7}}>This list calls <code>/api/positions</code> via the Gateway.</p>
    </div>
  );
}
